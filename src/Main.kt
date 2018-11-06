
object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        if (args.size < 3) {
            println("usage: <domain> <port> <web-folder>")
            System.exit(-1)
        }

        val server = Server(args[0], Integer.valueOf(args[1]), args[2])
        server.register(TimeRestController())
        server.run()
    }
}
