import org.w3c.dom.Element
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import kotlin.browser.document
import kotlin.browser.window
// web-ext run --source-dir ./extension-dist/
fun main(args: Array<String>) {
    println("Hello JavaScript!")
    // document.body?.style?.border = "1px solid white"
    val bw:dynamic = js("browser")
    /*
    Callback from getFileIcon.
    Initialize the displayed icon.
    */
    fun updateIconUrl(iconUrl:String) {
        var downloadIcon = document.querySelector("#icon")
        downloadIcon?.setAttribute("src", iconUrl)
    }
    fun onError(error:Any) {
        console.log("Error: $error")
    }
    /*
    Open the item using the associated application.
    */
    fun openItem(latestDownloadId:Int) {
        console.log("Got here!")
        val disabled: Boolean = document.querySelector("#open")?.classList?.contains("disabled") ?: true
        if(!disabled) {
            console.log("Opening...")
            bw.downloads.open(latestDownloadId)
        }
        console.log("latestDownloadId::End")
    }
    /*
    Remove item from disk (removeFile) and from the download history (erase)
    */
    fun removeItem(latestDownloadId:Int) {
        val disabled: Boolean = document.querySelector("#remove")?.classList?.contains("disabled") ?: true
        if (!disabled) {
            bw.downloads.removeFile(latestDownloadId)
            val o:dynamic = object{}
            o["id"] = latestDownloadId
            bw.downloads.erase(o)
            window.close()
        }
    }
    /*
    If there was a download item,
    - remember its ID as latestDownloadId
    - initialize the displayed icon using getFileIcon
    - initialize the displayed URL
    If there wasn't a download item, disable the "open" and "remove" buttons.
    */
    fun initializeLatestDownload(downloadItems:Array<Any>?) {
        var latestDownloadId:Int
        println("initializeLatestDownload::")
        var downloadUrl = document.querySelector("#url")
        if (downloadItems?.isNotEmpty() as Boolean) {
            println("initializeLatestDownload::NotEmpty")
            console.log(downloadItems.first())
            console.log("Test")
            var item:Any = downloadItems.first()
            console.log("Test1")
            latestDownloadId = item.asDynamic().id as Int? ?: 0
            console.log("Test2")
            println("latestDownloadId $latestDownloadId")
            var gettingIconUrl = bw.downloads.getFileIcon(latestDownloadId)
            gettingIconUrl.then(::updateIconUrl, ::onError)
            downloadUrl?.textContent = item.asDynamic().url as String? ?: "Failed2"
            document.querySelector("#open")?.addEventListener("click", {(::openItem)(latestDownloadId)})
            document.querySelector("#remove")?.addEventListener("click", {(::removeItem)(latestDownloadId)})
            console.log("initializeLatestDownload::End")
        } else {
            println("initializeLatestDownload::Empty")
            downloadUrl?.textContent = "No downloaded items found."
            document.querySelector("#icon")?.classList?.add("hidden")
            document.querySelector("#footer")?.classList?.add("hidden")
            console.log("initializeLatestDownload::EndEmpty")
        }
    }
    /*
    Search for the most recent download, and pass it to initializeLatestDownload()
    */
    val d:dynamic = object{}
    d["limit"] = 1
    d["orderBy"] = arrayOf("-startTime")
    var searching:dynamic = bw.downloads.search(d)
    searching.then(::initializeLatestDownload)
}