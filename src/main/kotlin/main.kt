import Utils.Requests
import java.io.File
import java.lang.Thread.sleep
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.ConcurrentHashMap
import javax.net.ssl.SSLProtocolException

fun main(args: Array<String>) {
    val req = Requests()

    val proxsMap = ConcurrentHashMap<String, Pair<Int, String>>()

    val prxs1 = req.getPrx("https://raw.githubusercontent.com/hookzof/socks5_list/master/proxy.txt").lines()
    val prxs2 = req.getPrx("https://raw.githubusercontent.com/TheSpeedX/PROXY-List/master/socks5.txt").lines()
    val prxs3 =
        req.getPrx("https://raw.githubusercontent.com/jetkai/proxy-list/main/online-proxies/txt/proxies-socks5.txt")
            .lines()
    val prxs4 = req.getPrx("https://raw.githubusercontent.com/roosterkid/openproxylist/main/SOCKS5_RAW.txt").lines()
    val prxs6 = req.getPrx("https://raw.githubusercontent.com/ShiftyTR/Proxy-List/master/socks5.txt").lines()
    val prxsSocks = prxs1 + prxs2 + prxs3 + prxs4 + prxs6 + File("proxisSocks5.txt").readLines()
    for (line in prxsSocks) {
        if (line.isNotBlank() && line.isNotEmpty()) {
            proxsMap[line.substringBefore(':')] = Pair(line.substringAfter(':').toInt(), "SOCKS5")
        }
    }
    for (i in 0..24) {
        proxsMap["127.0.0.1_$i"] = Pair((6001 + i), "SOCKS5")
    }
    val prxs7 = req.getPrx("https://raw.githubusercontent.com/ShiftyTR/Proxy-List/master/http.txt").lines()
    val prxs8 = req.getPrx("https://raw.githubusercontent.com/clarketm/proxy-list/master/proxy-list-raw.txt").lines()
    val prxs9 = req.getPrx("https://raw.githubusercontent.com/TheSpeedX/PROXY-List/master/http.txt").lines()
    val prxs10 =
        req.getPrx("https://raw.githubusercontent.com/jetkai/proxy-list/main/online-proxies/txt/proxies-http.txt")
            .lines()
    val prxsHttp = prxs7 + prxs8 + prxs9 + prxs10
    for (line in prxsHttp) {
        if (line.isNotBlank() && line.isNotEmpty()) {
            proxsMap[line.substringBefore(':')] = Pair(line.substringAfter(':').toInt(), "HTTP")
        }
    }

    while (true) {
        Thread {
            val prx = proxsMap.entries.shuffled().first()
            try {
                //req.sendNew(prx.key, prx.value)
                req.sendCom(prx.key.substringBefore('_'), prx.value)
            } catch (e: SocketException) {
            } catch (e: SocketTimeoutException) {
            } catch (e: UnknownHostException) {
            } catch (e: SSLProtocolException) {
            } catch (e: javax.net.ssl.SSLHandshakeException) {
            } catch (E: java.io.IOException) {
            }
        }.start()
        /*
        Thread {
            val prx = proxsMap.entries.shuffled().first()
            try {
                //req.sendNew(prx.key, prx.value)
                req.sendCom(prx.key, prx.value)
            } catch (e: SocketException) {
            } catch (e: SocketTimeoutException) {
            } catch (e: UnknownHostException) {
            } catch (e: SSLProtocolException) {
            } catch (e: javax.net.ssl.SSLHandshakeException) {
            } catch (E: java.io.IOException) {
            }
        }.start()
        Thread {
            val prx = proxsMap.entries.shuffled().first()
            try {
                //req.sendNew(prx.key, prx.value)
                req.sendNew(prx.key, prx.value)
            } catch (e: SocketException) {
            } catch (e: SocketTimeoutException) {
            } catch (e: UnknownHostException) {
            } catch (e: SSLProtocolException) {
            } catch (e: javax.net.ssl.SSLHandshakeException) {
            } catch (E: java.io.IOException) {
            }
        }.start()
         */
        sleep(10)
    }
}