

fun main() {
    val sn = SimpleNetwork()
    val brokerAddress = "broker://local"

    val broker = BrokerServer(sn, brokerAddress)
    // start components
    val imap = ImapComponent(sn, brokerAddress)
    val payment = PaymentComponent(sn, brokerAddress)
    val shipping = ShippingComponent(sn, brokerAddress)
    val order = OrderComponent(sn, brokerAddress)

    Thread.sleep(200)
    println("=== Simulate an order now ===")
    order.startOrder(listOf("itemA", "itemB", "itemC"), "ACC-987654")
    Thread.sleep(800)
    println("=== Demo finished ===")
}
