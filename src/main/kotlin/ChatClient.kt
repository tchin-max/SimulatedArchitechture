class ChatClient(sn: SimpleNetwork, val name: String, onData: (endPoint: EndPoint, data: Any) -> Unit = { _, _ -> }) {
    var id: Int = -1
    private val clientEndPoint: EndPoint?
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
                        is AssignId -> {
                            id = data.id
                            println("$name I got the id: '${data.id}'")
                        }
                        is SendMessage -> println("$name I got the message: '${data.msg}' from client with id '${data.fromId}'")
                    }
                    onData(endPoint, data)
                }

                override fun onClose(endPoint: EndPoint) {
                    println("$name connection on client side closed")
                }

                override fun onOpen(endPoint: EndPoint) {
                    println("$name connection established.")
                }
            })
    }

    // high level chat api for clients
    fun sendMessage(toId: Int, msg: String) {
        if (id >= 0) {
            this.sendData(SendMessage(msg, toId, id))
        }
    }

    // low level command interface
    fun sendData(data: Any) {
        clientEndPoint?.sendData(data)
    }

}
