import org.w3c.dom.Element
import org.w3c.dom.events.Event
import org.w3c.dom.events.EventListener
import kotlin.browser.document
import kotlin.browser.window
// web-ext run --source-dir ./extension-dist/
fun main(args: Array<String>) {
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
        val disabled: Boolean = document.querySelector("#open")?.classList?.contains("disabled") ?: true
        if(!disabled) {
            bw.downloads.open(latestDownloadId)
        }
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
        var downloadUrl = document.querySelector("#url")
        if (downloadItems?.isNotEmpty() as Boolean) {
            var item:Any = downloadItems.first()
            latestDownloadId = item.asDynamic().id as Int? ?: 0
            var gettingIconUrl = bw.downloads.getFileIcon(latestDownloadId)
            gettingIconUrl.then(::updateIconUrl, ::onError)
            downloadUrl?.textContent = item.asDynamic().url as String? ?: "Failed2"
            document.querySelector("#open")?.addEventListener("click", {(::openItem)(latestDownloadId)})
            document.querySelector("#remove")?.addEventListener("click", {(::removeItem)(latestDownloadId)})
        } else {
            downloadUrl?.textContent = "No downloaded items found."
            document.querySelector("#icon")?.classList?.add("hidden")
            document.querySelector("#footer")?.classList?.add("hidden")
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