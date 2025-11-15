
data class MsgRegister(val name: String)
data class PaymentRequest(val corrId: String, val account: String, val amount: Double)
data class PaymentResponse(val corrId: String, val success: Boolean)
data class ShipRequest(val corrId: String, val items: List<String>)
data class ShipResponse(val corrId: String, val success: Boolean)
data class Mail(val to: String, val subject: String, val body: String)
data class GenericForward(val to: String, val payload: Any)

class BrokerServer(sn: SimpleNetwork, val address: String) {

    private val components = mutableMapOf<String, EndPoint>()

    init {
        println("=== BrokerServer starting at $address ===")
        sn.provide(address, object : ConnectionHandler {
            override fun onOpen(endPoint: EndPoint) {
                println("Broker: component connected")
            }

            override fun onClose(endPoint: EndPoint) {
                // remove any registrations for this endpoint
                val removed = components.filterValues { it == endPoint }.keys
                removed.forEach {
                    components.remove(it)
                    println("Broker: unregistered $it")
                }
            }

            override fun onMsg(endPoint: EndPoint, msg: String) {
                // keep text API for backwards compat if used
                println("Broker (msg): $msg")
            }

            override fun onData(endPoint: EndPoint, data: Any) {
                when (data) {
                    is MsgRegister -> {
                        components[data.name] = endPoint
                        println("Broker: registered component '${data.name}'")
                        endPoint.sendData("REGISTERED:${data.name}")
                    }
                    is GenericForward -> {
                        val target = data.to
                        val payload = data.payload
                        val dest = components[target]
                        if (dest != null) {
                            dest.sendData(payload)
                        } else {
                            println("Broker: Unknown target '$target' for GenericForward")
                            endPoint.sendData("ERROR:Unknown target $target")
                        }
                    }
                    is PaymentRequest -> {
                        // forward to payment component
                        val paymentEP = components["payment"]
                        if (paymentEP != null) {
                            println("Broker: forward PaymentRequest ${data.corrId} to payment")
                            paymentEP.sendData(data)
                        } else {
                            println("Broker: no payment component registered")
                            endPoint.sendData(PaymentResponse(data.corrId, false))
                        }
                    }
                    is ShipRequest -> {
                        val shipEP = components["shipping"]
                        if (shipEP != null) {
                            println("Broker: forward ShipRequest ${data.corrId} to shipping")
                            shipEP.sendData(data)
                        } else {
                            println("Broker: no shipping component")
                            endPoint.sendData(ShipResponse(data.corrId, false))
                        }
                    }
                    is Mail -> {
                        val imap = components["imap"]
                        if (imap != null) {
                            imap.sendData(data)
                        } else {
                            println("Broker: no imap registered")
                        }
                    }
                    else -> {
                        println("Broker: onData unknown type: ${data::class.java.simpleName}")
                    }
                }
            }
        })
    }
}
