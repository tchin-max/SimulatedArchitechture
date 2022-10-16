class EchoServer(val sn: SimpleNetwork, val address: String) {
    init {
        println("========= Start up server ========= ")
        val connected = sn.provide(address, object : ConnectionHandler {

            override fun onMsg(endPoint: EndPoint, msg: String) {
                println("SERVER, got msg: '$msg'")
                endPoint.sendMsg("<$msg>")
            }

            override fun onClose(endPoint: EndPoint) {
                println("SERVER: closed")
            }

            override fun onOpen(endPoint: EndPoint) {
                println("SERVER: client connected for address $address")
            }

        })
        if (connected) {
            println("SERVER: listing to address $address")
        }
    }
}
