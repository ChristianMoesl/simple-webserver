import javax.swing.text.AbstractDocument
import java.io.*
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.stream.Collectors


class Server(hostname: String, port: Int, private val webFolder: String) {

    private val endPoint: InetSocketAddress
    private val requestPattern = Pattern.compile("([A-Z]+) (/[a-zA-Z0-9.]*) HTTP/1.1")
    private var socket: ServerSocket? = null
    private val controllers = Hashtable<String, Controller>()
    private var response: Response? = null

    init {
        endPoint = InetSocketAddress(hostname, port)
    }

    fun register(controller: Controller) {
        controllers[controller.path] = controller
    }

    private fun findForPath(path: String): Controller? {
        return controllers.get(path)
    }

    private fun serveFilesFor(path: String, requestType: RequestType): Boolean {
        if (requestType != RequestType.GET) {
            response = Response()
            return true
        }

        val searchPath = webFolder + path

        val file: Path
        if (path[path.length - 1] == '/')
            file = Paths.get(searchPath + "index.html")
        else
            file = Paths.get(searchPath)

        if (!Files.exists(file))
            return false

        val contentType = ContentType.forPath(file)

        response = if (contentType != null) {
            println("Serve static file $file")
            try {
                val resp = Response()
                resp.status = StatusCode.OK
                resp.contentType = contentType
                resp.content = Files.readAllBytes(file)
                resp
            } catch (e: IOException) {
                null
            }
        } else {
            null
        }

        return response != null
    }

    private fun executeControllerFor(path: String, requestType: RequestType): Boolean {
        val controller = findForPath(path)

        if (controller != null) {
            println("Executing controller \"" + controller.javaClass.simpleName + "\" for request")

            val request = Request(requestType, path)
            response = Response()

            if (request.type == RequestType.GET)
                controller.get(request, response!!)
            else
                controller.post(request, response!!)
        } else {
            response = null
        }

        return response != null
    }

    private fun serveNotFoundFor(path: String, requestType: RequestType) {
        response = Response()
    }

    private fun parseRequest(request: List<String>) {
        if (request.size > 0) {
            val line = request[0]
            val matcher = requestPattern.matcher(line)
            if (matcher.matches()) {
                val requestType = RequestType.forString(matcher.group(1))
                val path = matcher.group(2)

                if (requestType != null) {
                    println("Incoming Request: $requestType $path")

                    if (!serveFilesFor(path, requestType))
                        if (!executeControllerFor(path, requestType))
                            serveNotFoundFor(path, requestType)
                }
            } else {
                println("Request  \"$line\" not supported")
            }
        }
    }

    private fun receiveRequest(inputStream: InputStream): List<String> {
        try {
            val br = BufferedReader(InputStreamReader(inputStream))

            val lines = ArrayList<String>()
            var line = br.readLine()

            while (line != null && !line.isEmpty()) {
                lines.add(line)
                line = br.readLine()
            }

            return lines
        } catch (exception: IOException) {
            return emptyList()
        }

    }

    private fun sendResponse(outputStream: OutputStream) {
        try {
            outputStream.write(response!!.bytes)
            outputStream.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    @Throws(IOException::class)
    fun run() {
        socket = ServerSocket()
        socket!!.bind(endPoint, 100)

        while (true) {
            socket!!.accept().use { activeSocket ->
                val request = receiveRequest(activeSocket.getInputStream())

                parseRequest(request)

                if (response != null) {
                    sendResponse(activeSocket.getOutputStream())

                    response = null
                }
            }
        }
    }
}
