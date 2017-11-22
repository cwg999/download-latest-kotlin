# download-latest-kotlin
A conversion of the Mozilla Firefox download-latest web extension to Kotlin (and Kotlin Javascript Compilier). 

# Get Started

## Requirements

Requires `Intellij IDEA` for compiling, project management.

`node.js`,`npm` for web-ext running/testing.

`Firefox` for actually running the extension. 

## Running the code

Use IntelliJ IDEA, then hit Build -> Build Project (Ctrl+F9)

Load up the integrated console, (Alt+F12)

  `npm i -g web-ext`

  `web-ext run`

Firefox should load up and you should see a green plug-in icon in the top right. Clicking it will show you the latest download. 
To debug type in `about:debugging` in the url, then click "debug" --- to keep the popup from disappearing when you click it, click the 4 squares button next to the gear icon in the dev settings. 
