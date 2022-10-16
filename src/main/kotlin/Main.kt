const val address: String = "http://127.0.0.1:80/"

fun main(args: Array<String>) {

    val sn = SimpleNetwork()

    createAndStartServer(sn)

    val clientEndPoint1 = createAndStartClient1(sn)
    val clientEndPoint2 = createAndStartClient2(sn)

    println("========= Sending data ========= ")
    clientEndPoint1?.sendMsg("Test 1")
    clientEndPoint2?.sendMsg("Test 2")
}

private fun createAndStartServer(sn: SimpleNetwork) {
    println("========= Start up server ========= ")
    val clients: MutableMap<String, EndPoint> = HashMap()
    var freeId = 0
    val connected = sn.provide(address, object : ConnectionHandler {

        override fun onMsg(endPoint: EndPoint, msg: String) {
            println("SERVER, got msg: '$msg'")
            clients.values.forEach { client -> client.sendMsg("<$msg>") }
        }

        override fun onClose(endPoint: EndPoint) {
            println("SERVER: closed")
        }

        override fun onOpen(endPoint: EndPoint) {
            freeId += 1
            clients.put(freeId.toString(), endPoint)
            println("SERVER: client connected for address $address")
            endPoint.sendMsg("id: $freeId")
        }

    })
    if (connected) {
        println("SERVER: listing to address $address")
    }
}
private fun createAndStartClient1(sn: SimpleNetwork): EndPoint? {
    println("========= Start up client 1 ========= ")
    val clientEndPoint1 = sn.connect(address,
        object : ConnectionHandler {
            override fun onMsg(endPoint: EndPoint, msg: String) {
                println("CLIENT1: got msg: '$msg'")
            }

            override fun onClose(endPoint: EndPoint) {
                println("CLIENT1: connection on client side closed")
            }

            override fun onOpen(endPoint: EndPoint) {
                println("CLIENT1: connection established.")
            }
        })
    return clientEndPoint1
}
private fun createAndStartClient2(sn: SimpleNetwork): EndPoint? {
    println("========= Start up client 2 ========= ")
    val clientEndPoint2 = sn.connect(address,
        object : ConnectionHandler {
            var responded = false
            override fun onMsg(endPoint: EndPoint, msg: String) {
                println("CLIENT2: got msg: '$msg'")
                if (!responded) {
                    responded = true
                    endPoint.sendMsg("Hi there, I like to talk a lot.")
                }
            }

            override fun onClose(endPoint: EndPoint) {
                println("CLIENT2: connection on client side closed")
            }

            override fun onOpen(endPoint: EndPoint) {
                println("CLIENT2: connection established.")
            }
        })
    return clientEndPoint2
}

