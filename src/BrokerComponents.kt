
class OrderComponent(sn: SimpleNetwork, brokerAddress: String) {
    private lateinit var ep: EndPoint
    private var awaiting = mutableSetOf<String>() // corrIds waiting for shipping

    init {
        sn.connect(brokerAddress, object : ConnectionHandler {
            override fun onOpen(endPoint: EndPoint) {
                ep = endPoint
                // Register with broker
                ep.sendData(MsgRegister("order"))
            }

            override fun onMsg(endPoint: EndPoint, msg: String) {
                println("Order got msg: $msg")
            }

            override fun onData(endPoint: EndPoint, data: Any) {
                when (data) {
                    is PaymentResponse -> {
                        if (data.success) {
                            println("Order: Payment OK for ${data.corrId}")
                            // ask shipping
                            // items should be associated with corrId; for simplicity, we store corrId->items in awaiting map or pass items by payload
                            // We'll request shipping with a simple ShipRequest and keep corrId
                            ep.sendData(ShipRequest(data.corrId, listOf("items-for-${data.corrId}")))
                        } else {
                            println("Order: Payment FAILED for ${data.corrId}")
                            ep.sendData(Mail("customer@example.com", "Payment failed", "Payment failed for order ${data.corrId}"))
                        }
                    }
                    is ShipResponse -> {
                        if (data.success) {
                            println("Order: Shipping OK for ${data.corrId}")
                            ep.sendData(Mail("customer@example.com", "Order shipped", "Your order ${data.corrId} has been shipped."))
                        } else {
                            println("Order: Shipping FAILED for ${data.corrId}")
                            ep.sendData(Mail("customer@example.com", "Shipping failed", "Shipping failed for order ${data.corrId}."))
                        }
                    }
                    else -> {
                        println("Order: unknown data type ${data::class.simpleName}")
                    }
                }
            }

            override fun onClose(endPoint: EndPoint) {}
        })
    }

    // public API required by assignment:
    fun startOrder(items: List<String>, account: String) {
        val corr = "C" + System.currentTimeMillis().toString().takeLast(5)
        println("Order: starting order $corr for account $account with items $items")
        // For simplicity, compute amount as items.size * 10.0
        val amount = items.size * 10.0
        // send PaymentRequest via broker
        ep.sendData(PaymentRequest(corr, account, amount))
    }
}

// Payment Component
class PaymentComponent(sn: SimpleNetwork, brokerAddress: String) {
    init {
        sn.connect(brokerAddress, object : ConnectionHandler {
            override fun onOpen(endPoint: EndPoint) {
                endPoint.sendData(MsgRegister("payment"))
            }

            override fun onMsg(endPoint: EndPoint, msg: String) {}
            override fun onClose(endPoint: EndPoint) {}
            override fun onData(endPoint: EndPoint, data: Any) {
                when (data) {
                    is PaymentRequest -> {
                        println("Payment: processing ${data.corrId} amount=${data.amount} from ${data.account}")
                        // Simulate success (or add failure rules)
                        val success = true
                        // reply to broker: broker will route by sending PaymentResponse back to sender (order), but since payment doesn't know who requested it,
                        // we send GenericForward back to 'order' via broker: GenericForward(to="order", payload=PaymentResponse(...))
                        endPoint.sendData(GenericForward("order", PaymentResponse(data.corrId, success)))
                    }
                }
            }
        })
    }
}

// Shipping Component
class ShippingComponent(sn: SimpleNetwork, brokerAddress: String) {
    init {
        sn.connect(brokerAddress, object : ConnectionHandler {
            override fun onOpen(endPoint: EndPoint) {
                endPoint.sendData(MsgRegister("shipping"))
            }
            override fun onMsg(endPoint: EndPoint, msg: String) {}
            override fun onClose(endPoint: EndPoint) {}
            override fun onData(endPoint: EndPoint, data: Any) {
                when (data) {
                    is ShipRequest -> {
                        println("Shipping: shipping items for ${data.corrId} -> ${data.items}")
                        // simulate success
                        endPoint.sendData(GenericForward("order", ShipResponse(data.corrId, true)))
                    }
                }
            }
        })
    }
}

// Imap Component
class ImapComponent(sn: SimpleNetwork, brokerAddress: String) {
    init {
        sn.connect(brokerAddress, object : ConnectionHandler {
            override fun onOpen(endPoint: EndPoint) {
                endPoint.sendData(MsgRegister("imap"))
            }
            override fun onMsg(endPoint: EndPoint, msg: String) {}
            override fun onClose(endPoint: EndPoint) {}
            override fun onData(endPoint: EndPoint, data: Any) {
                when (data) {
                    is Mail -> {
                        println("IMAP: sending mail to ${data.to} | subject='${data.subject}' | body='${data.body}'")
                    }
                    is String -> {
                        // accept text-based mail if broker forwards strings
                        if (data.startsWith("MAIL:")) {
                            println("IMAP raw mail: $data")
                        }
                    }
                }
            }
        })
    }
}

