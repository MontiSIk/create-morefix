param(
    [Parameter(Mandatory=$true)][string]$Profile,
    [Parameter(Mandatory=$true)][string]$Libraries,
    [string]$Jdk = 'C:\Program Files\Java\jdk-21.0.11'
)
$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.IO.Compression.FileSystem
$project = Split-Path $PSScriptRoot -Parent
$output = Join-Path $project 'build/classes'
New-Item -ItemType Directory -Force -Path $output | Out-Null
$scratch = Join-Path ([IO.Path]::GetTempPath()) 'kriate-catwalk-compile'
New-Item -ItemType Directory -Force -Path $scratch | Out-Null
$create = Join-Path $Profile 'mods/create-1.21.1-6.0.10.jar'
$deco = Join-Path $Profile 'mods/createdeco-2.1.3.jar'
# Ponder/Catnip and Flywheel are bundled dependencies of Create.
$zip = [IO.Compression.ZipFile]::OpenRead($create)
try {
    foreach ($entry in $zip.Entries) {
        if ($entry.FullName -match '\.jar$') {
            $target = Join-Path $scratch ([IO.Path]::GetFileName($entry.FullName))
            [IO.Compression.ZipFileExtensions]::ExtractToFile($entry, $target, $true)
        }
    }
} finally { $zip.Dispose() }
foreach($name in @('sable-neoforge-1.21.1-2.0.3.jar','create-aeronautics-bundled-1.21.1-1.3.0.jar')) {
    $archive=Join-Path $Profile "mods/$name"
    $z=[IO.Compression.ZipFile]::OpenRead($archive)
    try {foreach($e in $z.Entries | Where-Object FullName -like '*.jar'){[IO.Compression.ZipFileExtensions]::ExtractToFile($e,(Join-Path $scratch ([IO.Path]::GetFileName($e.FullName))),$true)}}finally{$z.Dispose()}
}
# MixinExtras is already bundled with this NeoForge runtime; expose it only to javac.
foreach($runtime in Get-ChildItem -LiteralPath (Join-Path $Libraries 'net/neoforged/neoforge/21.1.228') -Filter '*universal.jar'){
    $z=[IO.Compression.ZipFile]::OpenRead($runtime.FullName)
    try {foreach($entry in $z.Entries | Where-Object FullName -Match 'mixinextras.*\.jar$'){[IO.Compression.ZipFileExtensions]::ExtractToFile($entry,(Join-Path $scratch ([IO.Path]::GetFileName($entry.FullName))),$true)}}finally{$z.Dispose()}
}
$jars = @((Join-Path $Profile 'mods/sable-neoforge-1.21.1-2.0.3.jar'),$create, $deco, (Join-Path $Profile 'mods/copycats-3.0.4+mc.1.21.1-neoforge.jar'))
$jars += @((Join-Path $Profile 'mods/forgematica-0.4.2+mc1.21.1.jar'),(Join-Path $Profile 'mods/mafglib-0.4.3+mc1.21.1.jar'))
$jars += Get-ChildItem -LiteralPath $scratch -Filter '*.jar' | ForEach-Object {$_.FullName}
# Prioritize this exact Minecraft/NeoForge version before other cached versions.
$jars += Get-ChildItem -LiteralPath $Libraries -Recurse -Filter '*1.21.1*srg.jar' | ForEach-Object {$_.FullName}
$jars += Get-ChildItem -LiteralPath $Libraries -Recurse -Filter '*21.1.228*universal.jar' | ForEach-Object {$_.FullName}
$jars += Get-ChildItem -LiteralPath $Libraries -Recurse -Filter '*.jar' | ForEach-Object {$_.FullName}
$classpath = ($jars | Select-Object -Unique) -join ';'
$sources = Get-ChildItem -LiteralPath (Join-Path $project 'src/main/java') -Recurse -Filter '*.java'
$arguments = @('--release', '21', '-proc:none', '-encoding', 'UTF-8', '-classpath', ('"'+$classpath.Replace('\','/')+'"'), '-d', 'build/classes')
$arguments += $sources | ForEach-Object {'"'+[IO.Path]::GetRelativePath($project,$_.FullName).Replace('\','/')+'"'}
$argumentFile = Join-Path $scratch 'javac.args'
[IO.File]::WriteAllLines($argumentFile, $arguments, [Text.UTF8Encoding]::new($false))
Push-Location $project
try { & "$Jdk/bin/javac.exe" "@$argumentFile" } finally { Pop-Location }
if ($LASTEXITCODE -ne 0) { throw "javac failed: $LASTEXITCODE" }
$materials = @('andesite','brass','copper','industrial_iron','iron','zinc')
$rotations = [ordered]@{
    up = @{x=0;y=0}; down = @{x=180;y=0}; north = @{x=90;y=0};
    east = @{x=90;y=90}; south = @{x=90;y=180}; west = @{x=90;y=270}
}
$states = Join-Path $output 'assets/createdeco/blockstates'
$models = Join-Path $output 'assets/catwalk_orientation/models/block'
New-Item -ItemType Directory -Force -Path $states | Out-Null
New-Item -ItemType Directory -Force -Path $models | Out-Null
foreach ($material in $materials) {
    $parts = @()
    foreach ($face in $rotations.Keys) {
        $rotation = $rotations[$face]
        $parts += @{when=@{facing=$face}; apply=@{model="createdeco:block/${material}_catwalk"; x=$rotation.x; y=$rotation.y}}
        $parts += @{when=@{facing=$face; bottom='true'}; apply=@{model="createdeco:block/${material}_catwalk_support"; x=$rotation.x; y=$rotation.y}}
        $edges=[ordered]@{north=90;east=180;south=270;west=0}
        foreach($edge in $edges.Keys) {
            $modelName="${material}_${face}_${edge}"
            $condition=@{facing=$face};$condition["rail_$edge"]='true'
            $parts+=@{when=$condition;apply=@{model="catwalk_orientation:block/$modelName"}}
            $model=@{parent="createdeco:block/${material}_catwalk_railing";loader='catwalk_orientation:railing';reference="createdeco:block/${material}_catwalk_railing";mount_x=$rotation.x;mount_y=$rotation.y;edge_y=$edges[$edge]}
            [IO.File]::WriteAllText((Join-Path $models "$modelName.json"),($model | ConvertTo-Json -Depth 5),[Text.UTF8Encoding]::new($false))
        }
    }
    $json = @{multipart=$parts} | ConvertTo-Json -Depth 8
    [IO.File]::WriteAllText((Join-Path $states "${material}_catwalk.json"), $json, [Text.UTF8Encoding]::new($false))
}
Copy-Item -Path (Join-Path $project 'src/main/resources/*') -Destination $output -Recurse -Force
# Keep original asset references; ship only additional state mappings for the new axes.
foreach ($assetJar in @($create,$deco,(Join-Path $Profile 'mods/Create Encased-1.21.1-1.9.0-ht2.jar'))) {
    if (!(Test-Path -LiteralPath $assetJar)) {continue}
    $assetZip=[IO.Compression.ZipFile]::OpenRead($assetJar)
    try {
        foreach ($entry in $assetZip.Entries) {
            if ($entry.FullName -notmatch '^assets/([^/]+)/blockstates/(.*fluid_tank|.*shipping_container|item_vault)\.json$') {continue}
            $namespace=$Matches[1];$name=$Matches[2]
            $reader=[IO.StreamReader]::new($entry.Open())
            try {$original=$reader.ReadToEnd() | ConvertFrom-Json -AsHashtable} finally {$reader.Dispose()}
            if (!$original.ContainsKey('variants')) {throw "Unexpected storage model format: $($entry.FullName)"}
            $variants=[ordered]@{}
            foreach ($key in $original.variants.Keys) {
                $model=$original.variants[$key]
                if($name -eq 'item_vault' -or $name -like '*shipping_container') {
                    $variants[$key]=$model
                    if($key -match 'axis=z') {
                        $copy=($model | ConvertTo-Json -Depth 12 | ConvertFrom-Json -AsHashtable)
                        $copy.x=90;$copy.y=0;$copy.uvlock=$false
                        if($name -eq 'item_vault') {$copy=@{model='catwalk_orientation:block/vertical_item_vault'}}
                        $variants[$key.Replace('axis=z','axis=y')]=$copy
                    }
                } else {
                    foreach($axis in @('y','x','z')) {
                        $copy=($model | ConvertTo-Json -Depth 12 | ConvertFrom-Json -AsHashtable)
                        if($axis -ne 'y') {$copy.x=if($axis -eq 'x'){90}else{270};$copy.y=if($axis -eq 'x'){90}else{0};$copy.uvlock=$false}
                        $variants["axis=$axis,$key"]=$copy
                    }
                }
            }
            $target=Join-Path $output "assets/$namespace/blockstates"
            New-Item -ItemType Directory -Force $target | Out-Null
            [IO.File]::WriteAllText((Join-Path $target "$name.json"),(@{variants=$variants} | ConvertTo-Json -Depth 16),[Text.UTF8Encoding]::new($false))
        }
    } finally {$assetZip.Dispose()}
}
# Add vertical models for every installed Steam n Rails locomotive boiler.
$railways=Join-Path $Profile 'mods/railways-0.2.0+neoforge-mc1.21.1.jar'
if(Test-Path -LiteralPath $railways){
    $z=[IO.Compression.ZipFile]::OpenRead($railways)
    try {foreach($entry in $z.Entries){
        if($entry.FullName -notmatch '^assets/railways/blockstates/(.*locometal_boiler)\.json$'){continue}
        $name=$Matches[1];$r=[IO.StreamReader]::new($entry.Open())
        try {$json=$r.ReadToEnd() | ConvertFrom-Json -AsHashtable} finally {$r.Dispose()}
        foreach($key in @($json.variants.Keys)){if($key -notlike 'axis=z,*'){continue}
            $model=$json.variants[$key] | ConvertTo-Json -Depth 12 | ConvertFrom-Json -AsHashtable
            $model.x=90;$model.y=0;$model.uvlock=$false
            $json.variants[$key.Replace('axis=z','axis=y')]=$model
        }
        $target=Join-Path $output 'assets/railways/blockstates';New-Item -ItemType Directory -Force $target | Out-Null
        [IO.File]::WriteAllText((Join-Path $target "$name.json"),($json | ConvertTo-Json -Depth 16),[Text.UTF8Encoding]::new($false))
    }} finally {$z.Dispose()}
}
# Keep the rail-facing wheel assembly fixed, and map the independent shaft housing.
$z=[IO.Compression.ZipFile]::OpenRead($create)
try {
    $reader=[IO.StreamReader]::new($z.GetEntry('assets/create/blockstates/gantry_carriage.json').Open())
    try {$native=$reader.ReadToEnd() | ConvertFrom-Json -AsHashtable} finally {$reader.Dispose()}
    $variants=[ordered]@{}
    foreach($key in $native.variants.Keys){
        $variants["$key,shaft_along_mount=false"]=$native.variants[$key]
        $face=($key -split 'facing=')[1]
        $rotation=$rotations[$face]
        $variants["$key,shaft_along_mount=true"]=@{model='catwalk_orientation:block/gantry_axial';x=$(switch($face){'up'{90};'down'{270};default{0}});y=$(switch($face){'east'{270};'north'{180};'west'{90};default{0}})}
    }
    $target=Join-Path $output 'assets/create/blockstates';New-Item -ItemType Directory -Force $target | Out-Null
    [IO.File]::WriteAllText((Join-Path $target 'gantry_carriage.json'),(@{variants=$variants}|ConvertTo-Json -Depth 10),[Text.UTF8Encoding]::new($false))
} finally {$z.Dispose()}
$dist = Join-Path $project 'dist'
New-Item -ItemType Directory -Force -Path $dist | Out-Null
$jar = Join-Path $dist 'create-more-fix-1.5.15-mc1.21.1.jar'
Push-Location $project
try { & "$Jdk/bin/jar.exe" --create --file 'dist/create-more-fix-1.5.15-mc1.21.1.jar' -C 'build/classes' . } finally { Pop-Location }
if ($LASTEXITCODE -ne 0) { throw "jar failed: $LASTEXITCODE" }
Write-Output $jar






