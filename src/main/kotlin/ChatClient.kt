class ChatClient(sn: SimpleNetwork, name: String) {
    val clientEndPoint: EndPoint?
    init {
        println("========= Start up client 1 ========= ")
        clientEndPoint = sn.connect(address,
            object : ConnectionHandler {
                override fun onMsg(endPoint: EndPoint, msg: String) {
                    println("$name: got msg: '$msg'")
                }

                override fun onData(endPoint: EndPoint, data: Any) {
                    println("$name got data: '$data'")
                    when (data) {
                        is AssignId -> println("$name I got the id: '${data.id}'")
                        is SendMessage -> println("$name I got the message: '${data.msg}' from client with id '${data.fromId}'")
                    }
                }

                override fun onClose(endPoint: EndPoint) {
                    println("$name connection on client side closed")
                }

                override fun onOpen(endPoint: EndPoint) {
                    println("$name connection established.")
                }
            })
    }

    // low level command interface
    fun sendData(data: Any) {
        clientEndPoint?.sendData(data)
    }

}
