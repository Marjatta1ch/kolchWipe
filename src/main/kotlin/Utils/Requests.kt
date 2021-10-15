package Utils

import okhttp3.*
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.URLEncoder
import java.util.*

class Requests {

    fun getCookies(proxyIp: String = "", proxyPort: Pair<Int, String> = Pair(0, "")): Pair<String, String> {
        val proxyWipe = if(proxyPort.second == "SOCKS5") {
            Proxy(Proxy.Type.SOCKS, InetSocketAddress.createUnresolved(proxyIp, proxyPort.first))
        } else {
            Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(proxyIp, proxyPort.first))
        }

        val client = OkHttpClient().newBuilder().proxy(proxyWipe).retryOnConnectionFailure(true).build()
        var cookies = Pair<String, String>("", "")

        val request = Request.Builder()
            .url("https://1chan.top/")
            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; rv:78.0) Gecko/20100101 Firefox/78.0")
            .build()

        val response = client.newCall(request).execute()

        for ((name, value) in response.headers) {
            if (name.contains("set-cookie")) {
                if (value.contains("key=")) {
                    cookies = cookies.copy(first = value.substringAfter("key=").substringBefore(';'))
                }
                if (value.contains("PHPSESSID=")) {
                    cookies = cookies.copy(second = value.substringAfter("PHPSESSID=").substringBefore(';'))
                }
            }
        }
        val rsp = response.body!!.string()
        if(rsp.contains("https://www.cloudflare.com/5xx-error-landing/") && rsp.contains("Please turn JavaScript on and reload the page.") && rsp.contains("DDoS protection by")){
            ThreadDeath()
        }
        return (cookies)
    }

    fun getPrx(url: String): String{
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .build()
        val response = client.newCall(request).execute()
        return response.body!!.string()
    }

    fun solveCaptcha(key: String, PHPSESSID: String, proxyIp: String, proxyPort: Pair<Int, String>): String{
        val proxyWipe = if(proxyPort.second == "SOCKS5") {
            Proxy(Proxy.Type.SOCKS, InetSocketAddress.createUnresolved(proxyIp, proxyPort.first))
        } else {
            Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(proxyIp, proxyPort.first))
        }

        val request = Request.Builder()
            .url("https://1chan.top/captcha/?key=post&PHPSESSID=$PHPSESSID")
            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; rv:78.0) Gecko/20100101 Firefox/78.0")
            .header("Cookie", "key=$key; homeboard=krautchan.net; PHPSESSID=$PHPSESSID")
            .build()

        val image = Base64.getEncoder().encodeToString(OkHttpClient().newBuilder().proxy(proxyWipe).retryOnConnectionFailure(true).build().newCall(request).execute().body!!.bytes())
        val requestCaptcha = Request.Builder()
            .url("http://127.0.0.1:5000/solve?image=${URLEncoder.encode(image)}")
            .build()
        return OkHttpClient().newCall(requestCaptcha).execute().body!!.string().substringAfter("\"").substringBefore("\"")
    }

    fun sendNew(proxyIp: String, proxyPort: Pair<Int, String>){
        val proxyWipe = if(proxyPort.second == "SOCKS5") {
            Proxy(Proxy.Type.SOCKS, InetSocketAddress.createUnresolved(proxyIp, proxyPort.first))
        } else {
            Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(proxyIp, proxyPort.first))
        }

        val keyPHPSESSID = getCookies(proxyIp, proxyPort)

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("email", "")
            .addFormDataPart("homeboard", "krautchan.net")
            .addFormDataPart("category", "")
            .addFormDataPart("title", "КАЛОВАЯ ОТСИРАЛЬНАЯ")//"НЯШНЫЙ+ЧАТИК+НЯШУЛИЕВ")
            .addFormDataPart("link", "https://2.0-chan.ru/")
            .addFormDataPart("text", "ПРРРРРРРРРРРРРРРРРРРРРРРРРРР ПССССССССССССССССССССССССССС [:6Y1xrpY:]")//"Няшулий,+прмсоединяйся+к+няшному+чату+няшулиев ")
            .addFormDataPart("text_full", "КАЛОВАЯ ОТСИРАЛЬНАЯ")//"НЯШНЫЙ+ЧАТИК+НЯШУЛИЕВ")
            .addFormDataPart("captcha_key", "post")
            .addFormDataPart("captcha", solveCaptcha(keyPHPSESSID.first, keyPHPSESSID.second, proxyIp, proxyPort))
            .build()

        val request = Request.Builder()
            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; rv:78.0) Gecko/20100101 Firefox/78.0")
            .header("Referer", "https://1chan.top/news/")
            .header("Cookie", "PHPSESSID=${keyPHPSESSID.second}; key=${keyPHPSESSID.first}; homeboard=krautchan.net")
            .url("https://1chan.top/news/add/")
            .post(requestBody)
            .build()

        OkHttpClient().newBuilder().proxy(proxyWipe).retryOnConnectionFailure(true).build().newCall(request).execute().body!!.close()
    }
}