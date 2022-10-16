fun log(msg: String) {
    // println("   nc: $msg")
}
interface Network {
    fun connect(address: String, handler: ConnectionHandler): EndPoint?
    fun provide(address: String, handler: ConnectionHandler): Boolean
    fun remove(address: String, handler: ConnectionHandler): Boolean
}
interface ConnectionHandler {
    fun onMsg(endPoint: EndPoint, msg: String)
    fun onClose(endPoint: EndPoint)
    fun onOpen(endPoint: EndPoint)
}
interface EndPoint {
    fun sendMsg(msg: String): Unit
    fun close()
}

class SimpleNetwork : Network {
    private val connectionProviders: MutableMap<String, ConnectionProvider> = HashMap()
    private val handler: MutableMap<String, ConnectionHandler> = HashMap()

    override fun connect(address: String, handler: ConnectionHandler): EndPoint? {
        connectionProviders[address]?.let { s ->
            log("sn: For address $address a connection provider is found.")
            // there is a server, create the connection, and return the endpoint
            val nc = s.open()
            val clientEndPoint = nc.leftEndPoint
            nc.connectLeftSide(
                { msg -> clientEndPoint.let { handler.onMsg(it, msg) } },
                { clientEndPoint.let {handler.onClose(it) } },
                { handler.onOpen(clientEndPoint) })
            log("sn: Connection created by network for the address $address, and an endpoint is provided to the client.")
            return clientEndPoint
        }

        log("For the address $address there is no server registered.")
        // no server, return null
        return null
    }
    override fun provide(address: String, handler: ConnectionHandler): Boolean {
        if (connectionProviders.containsKey(address)) {
            log("For address $address there is already another connection provider registered. Nothing happend.")
            return false
        }
        val cp = SimpleNetworkConnectionProvider(handler)
        connectionProviders[address] = cp
        this.handler[address] = handler
        log("For address $address a connection provider is registered.")
        return true
    }
    override fun remove(address: String, handler: ConnectionHandler): Boolean {
        if (connectionProviders.containsKey(address) && this.handler[address] == handler) {
            val cp = connectionProviders[address]
            this.handler.remove(address)
            connectionProviders.remove(address)
            log("For address $address a connection provider is removed.")
            return true
        }
        log("For address $address there was no connection provider. Nothing happend.")
        return false
    }

}
private typealias MsgSender = (msg: String) -> Unit
private interface ConnectionProvider {
    fun open(): NetworkConnection
}
private class SimpleNetworkConnectionProvider(private val handler: ConnectionHandler): ConnectionProvider {
    override fun open(): NetworkConnection {
        val snc = SimpleNetworkConnection()
        var rightEndPoint: EndPoint? = snc.rightEndPoint
        snc.connectRightSide({
            msg -> log("ncp, got msg: $msg")
            rightEndPoint?.let { handler.onMsg(it, msg) }
         }, {
            log("ncp: onClose")
            rightEndPoint?.let {
                handler.onClose(it)
                rightEndPoint = null
            }
        }, {
            rightEndPoint?.let { handler.onOpen(it) }
        })
        //rightEndPoint?.let { handler.onOpen(it) }
        // we cannot call the open method here, because
        // left endpoint is not connected. We have to call
        // it in the code in the network itself
        return snc
    }

}
private interface NetworkConnection {
    fun connectLeftSide(receiver: MsgSender, onClose: () -> Unit, onOpen: () -> Unit)
    fun connectRightSide(receiver: MsgSender, onClose: () -> Unit, onOpen: () -> Unit)
    val leftEndPoint: EndPoint
    val rightEndPoint: EndPoint
}
class SimpleNetworkConnection : NetworkConnection {
    private var closed: Boolean = false
    var leftReceiver: MsgSender? = null
    var leftOnClose : (() -> Unit)? = null
    var rightReceiver: MsgSender? = null
    var rightOnClose : (() -> Unit)? = null
    var leftOnOpen: (() -> Unit)? = null
    var rightOnOpen: (() -> Unit)? = null
    public override val leftEndPoint: EndPoint = object : EndPoint {
        override fun sendMsg(msg: String) {
            rightReceiver?.let { lr -> lr(msg) }
        }

        override fun close() {
            this@SimpleNetworkConnection.close()
        }

    }
    public override val rightEndPoint: EndPoint = object : EndPoint {
        override fun sendMsg(msg: String) {
            leftReceiver?.let { rr -> rr(msg) }
        }

        override fun close() {
            this@SimpleNetworkConnection.close()
        }

    }

    override fun connectLeftSide(receiver: MsgSender, onClose: () -> Unit, onOpen: () -> Unit) {
        leftReceiver = receiver
        this.leftOnClose = onClose
        this.leftOnOpen = onOpen

        if (leftReceiver != null && rightReceiver != null) {
            this.leftOnOpen?.let { it() }
            this.rightOnOpen?.let { it() }
        }
    }

    override fun connectRightSide(receiver: MsgSender, onClose: () -> Unit, onOpen: () -> Unit) {
        rightReceiver = receiver
        this.rightOnClose = onClose
        this.rightOnOpen = onOpen

        if (leftReceiver != null && rightReceiver != null) {
            this.leftOnOpen?.let { it() }
            this.rightOnOpen?.let { it() }
        }
    }

    fun close() {
        this.rightOnClose?.let { it() }
        this.leftOnClose?.let { it() }
        this.rightOnClose = null
        this.leftOnClose = null
        this.leftReceiver = null
        this.rightReceiver = null
        this.leftOnOpen = null
        this.rightOnOpen = null
        this.closed = true
    }

}
