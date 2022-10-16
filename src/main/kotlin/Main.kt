const val address: String = "http://127.0.0.1:80/"

fun main(args: Array<String>) {

    val sn = SimpleNetwork()

    val cs: ChatServer = ChatServer(sn, address)

    val c1 = ChatClient(sn, "CLIENT1")
    val c2 = ChatClient(sn, "CLIENT2") { eP, data ->
        when (data) {
            is SendMessage -> {
                eP.sendData("Pong")
            }
        }
    }

    println("========= Sending data ========= ")
    c1.sendData("Test 1")
    c2.sendData("Test 2")
    c2.sendData(SendMessage("Hallo, ich bin 2, wer bist Du?", 1, 2))
    c1.sendData(SendMessage("Hallo, ich bin 1, wer bist Du?", 2, 1))
}
