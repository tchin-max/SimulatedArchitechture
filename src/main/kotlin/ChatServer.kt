class ChatServer(sn: SimpleNetwork, address: String) {
    init {
        println("========= Start up chat server at $address ========= ")
        // 'Send to all Server'
        // Map stores the EndPoints for the ids assigned to the clients
        val clients: MutableMap<String, EndPoint> = HashMap()
        var freeId = 1 // the id used for the next client

        val connected = sn.provide(address, object : ConnectionHandler {

            override fun onMsg(endPoint: EndPoint, msg: String) {
                println("SERVER ($address), got msg: '$msg'")

                // TODO: parse message, so that we can send it to the correct clients the massage

            }

            override fun onClose(endPoint: EndPoint) {
                println("SERVER ($address): closed")
            }

            override fun onOpen(endPoint: EndPoint) {
                println("SERVER ($address): client connected.")
                // 'Send to all server with ids'
                clients.put(freeId.toString(), endPoint)
                endPoint.sendMsg("id: $freeId")
                freeId += 1
            }

        })
        if (connected) {
            println("SERVER ($address) started, listing to address $address")
        }

    }
}
