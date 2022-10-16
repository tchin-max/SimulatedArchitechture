const val address: String = "http://127.0.0.1:80/"

fun main(args: Array<String>) {

    val sn = SimpleNetwork()

    val cs: ChatServer = ChatServer(sn, address)

    val clientEndPoint1 = createAndStartClient1(sn)
    val clientEndPoint2 = createAndStartClient2(sn)

    println("========= Sending data ========= ")
    clientEndPoint1?.sendMsg("Test 1")
    clientEndPoint2?.sendMsg("Test 2")
    clientEndPoint2?.sendData(SendMessage("Hallo, ich bin 2, wer bist Du?", 1, 2))
}

private fun createAndStartClient1(sn: SimpleNetwork): EndPoint? {
    println("========= Start up client 1 ========= ")
    val clientEndPoint1 = sn.connect(address,
        object : ConnectionHandler {
            override fun onMsg(endPoint: EndPoint, msg: String) {
                println("CLIENT1: got msg: '$msg'")
            }

            override fun onData(endPoint: EndPoint, data: Any) {
                println("CLIENT1: got data: '$data'")
                when (data) {
                    is AssignId -> println("CLIENT1: I got the id: '${data.id}'")
                    is SendMessage -> println("CLIENT1: I got the message: '${data.msg}' from client with id '${data.fromId}'")
                }
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
                if (msg.startsWith("id: ")) {
                    val id = Integer.parseInt(msg.substring(4))
                    println("CLIENT2: I got the id: $id")
                    return
                }
                println("CLIENT2: got msg: '$msg'")
                if (!responded) {
                    responded = true
                    endPoint.sendMsg("Hi there, I like to talk a lot.")
                }
            }

            override fun onData(endPoint: EndPoint, data: Any) {
                println("CLIENT1: got data: '$data'")
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

