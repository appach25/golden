$files = Get-ChildItem -Path "src\main\java\com\golden\system" -Recurse -Filter "*.java"
foreach ($file in $files) {
    (Get-Content $file.FullName) | ForEach-Object {
        $_ -replace 'com\.golden\.sytem', 'com.golden.system'
    } | Set-Content $file.FullName
}
