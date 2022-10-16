class ChatServer(sn: SimpleNetwork, address: String) {
    init {
        println("========= Start up chat server at $address ========= ")
        // 'Send to all Server'
        // Map stores the EndPoints for the ids assigned to the clients
        val clients: MutableMap<Int, EndPoint> = HashMap()
        val endPointsToIds: MutableMap<EndPoint, Int> = HashMap()
        var freeId = 1 // the id used for the next client

        val connected = sn.provide(address, object : ConnectionHandler {

            override fun onMsg(endPoint: EndPoint, msg: String) {
                println("SERVER ($address), got msg: '$msg'")
            }

            override fun onData(endPoint: EndPoint, data: Any) {
                println("SERVER ($address), got data: '$data'")
                when (data) {
                    is SendMessage -> {
                        val toId = data.toId
                        val client = clients[toId]
                        val fromId = endPointsToIds[endPoint]
                        // ignore if toId is wrong. Another solution: patch it??
                        if (fromId == data.fromId) {
                            client?.sendData(data)
                        }
                    }
                }
            }

            override fun onClose(endPoint: EndPoint) {
                println("SERVER ($address): closed")
            }

            override fun onOpen(endPoint: EndPoint) {
                println("SERVER ($address): client connected.")
                // 'Send to all server with ids'
                clients.put(freeId, endPoint)
                endPointsToIds.put(endPoint, freeId)
                endPoint.sendData(AssignId(freeId))
                freeId += 1
            }

        })
        if (connected) {
            println("SERVER ($address) started, listing to address $address")
        }
    }
}

data class AssignId(val id: Int)
data class SendMessage(val msg: String, val toId: Int, val fromId: Int)
