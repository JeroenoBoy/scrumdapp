#!/bin/powershell
$sourceDir = "$PWD\unprocessed"
$destDir = "$PWD"
$thumbsDir = "$PWD\thumbnails"

# Create the destination and thumbnails directories if they don't exist
if (-not (Test-Path $destDir)) {
    New-Item -ItemType Directory -Path $destDir
}

if (-not (Test-Path $thumbsDir)) {
    New-Item -ItemType Directory -Path $thumbsDir
}

Write-Host "In: $sourceDir"
Write-Host "Out: $destDir"
Write-Host "Thumb: $thumbsDir"

# Get all image files in the source directory
$imageFiles = Get-ChildItem -Path $sourceDir -File

Write-Host "Images: $imageFiles"

foreach ($image in $imageFiles) {
    # Define the output file names
    $webpFile = Join-Path -Path $destDir -ChildPath ($image.BaseName + ".webp")
    $thumbFile = Join-Path -Path $thumbsDir -ChildPath ($image.BaseName + ".webp")

    # Convert the image to WebP format
    ffmpeg -i $image.FullName -c:v libwebp -quality 80 $webpFile

    # Create a 420p thumbnail
    ffmpeg -i $image.FullName -vf "scale=420:-1" -c:v libwebp -quality 80 $thumbFile
    Write-Host "Converted $webpFile"
}

Write-Host "Conversion complete. WebP images and thumbnails are saved."