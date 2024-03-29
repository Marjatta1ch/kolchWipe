package Utils

import okhttp3.*
import org.jsoup.Jsoup
import java.io.File
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.URLEncoder
import java.util.*

val psts = File("doto").readLines() + File("dt").readLines() + File("plaksa").readLines() + File("okor").readLines() + File("kudah").readLines()

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
        if(!response.isSuccessful){
            response.body!!.close()
            throw IOException()
        }
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
            throw IOException()
        }
        return (cookies)
    }

    fun getCookiesForCom(proxyIp: String = "", proxyPort: Pair<Int, String> = Pair(0, "")): Pair<Pair<String, String>, MutableList<String>> {
        val proxyWipe = if(proxyPort.second == "SOCKS5") {
            Proxy(Proxy.Type.SOCKS, InetSocketAddress.createUnresolved(proxyIp, proxyPort.first))
        } else {
            Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(proxyIp, proxyPort.first))
        }

        val client = OkHttpClient().newBuilder().proxy(proxyWipe).retryOnConnectionFailure(true).build()
        var cookies = Pair<String, String>("", "")

        val request = Request.Builder()
            .url("https://1chan.top/news/all/")
            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; rv:78.0) Gecko/20100101 Firefox/78.0")
            .build()

        val response = client.newCall(request).execute()
        if(!response.isSuccessful){
            response.body!!.close()
            throw IOException()
        }
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
        val posts = mutableListOf<String>()
        if(rsp.contains("https://www.cloudflare.com/5xx-error-landing/") && rsp.contains("Please turn JavaScript on and reload the page.") && rsp.contains("DDoS protection by")){
            throw IOException()
        } else {
            val news = Jsoup.parse(rsp)
            val postsNmbrs = news.select("a[class=g-disabled][href]")
            for(post in postsNmbrs){
                posts.add(post.text().substringAfter('№'))
            }
            posts.add("2096540")
            posts.add("2092773")
            posts.add("2092333")
            posts.add("2089637")
            posts.add("2088960")
            posts.add("2079396")
            posts.add("2079297")
            posts.add("2079012")
            posts.add("2079300")
            posts.add("2076520")
            posts.add("2078742")
            posts.add("2078659")
            posts.add("2076844")
            posts.add("2073786")
            posts.add("2073084")
            posts.add("2070089")
            posts.add("2067834")
            posts.add("2065994")
            posts.add("2065387")
            posts.add("2064099")
            posts.add("2064036")
            posts.add("2064579")
            posts.add("2062243")
            posts.add("2060310")
            posts.add("2059564")
            posts.add("2094008")
        }
        return (Pair(cookies, posts))
    }

    fun getPrx(url: String): String{
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .build()
        val response = client.newCall(request).execute()
        return response.body!!.string()
    }

    fun solveCaptcha(key: String, PHPSESSID: String, proxyIp: String, proxyPort: Pair<Int, String>, is_com: Boolean = false): String{
        val proxyWipe = if(proxyPort.second == "SOCKS5") {
            Proxy(Proxy.Type.SOCKS, InetSocketAddress.createUnresolved(proxyIp, proxyPort.first))
        } else {
            Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(proxyIp, proxyPort.first))
        }

        val request = Request.Builder()
            .url(if(!is_com)"https://1chan.top/captcha/?key=post&PHPSESSID=$PHPSESSID" else "https://1chan.top/captcha/?key=comment&PHPSESSID=$PHPSESSID")
            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; rv:78.0) Gecko/20100101 Firefox/78.0")
            .header("Cookie", "key=$key; homeboard=krautchan.net; PHPSESSID=$PHPSESSID")
            .build()
        val rsp = OkHttpClient().newBuilder().proxy(proxyWipe).retryOnConnectionFailure(true).build().newCall(request).execute()
        if(!rsp.isSuccessful){
            rsp.body!!.close()
            throw IOException()
        }
        val image = Base64.getEncoder().encodeToString(rsp.body!!.bytes())

        val requestCaptcha = Request.Builder()
            .url("http://127.0.0.1:5000/solve?image=${URLEncoder.encode(image)}")
            .build()
        val respCaptch = OkHttpClient().newCall(requestCaptcha).execute()
        if(!respCaptch.isSuccessful){
            respCaptch.body!!.close()
            throw IOException()
        }
        return respCaptch.body!!.string().substringAfter("\"").substringBefore("\"")
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
            .addFormDataPart("title", psts.random().take(69))//"НЯШНЫЙ+ЧАТИК+НЯШУЛИЕВ")
            .addFormDataPart("link", "")
            .addFormDataPart("text", psts.random().take(900))//"Няшулий,+прмсоединяйся+к+няшному+чату+няшулиев ")
            .addFormDataPart("text_full", psts.random().take(900))//"НЯШНЫЙ+ЧАТИК+НЯШУЛИЕВ")
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

    fun sendCom(proxyIp: String, proxyPort: Pair<Int, String>){
        val proxyWipe = if(proxyPort.second == "SOCKS5") {
            Proxy(Proxy.Type.SOCKS, InetSocketAddress.createUnresolved(proxyIp, proxyPort.first))
        } else {
            Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(proxyIp, proxyPort.first))
        }

        val keyPHPSESSID = getCookiesForCom(proxyIp, proxyPort)
        val post = keyPHPSESSID.second.random()
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("email", "")
            .addFormDataPart("homeboard", "krautchan.net")
            .addFormDataPart("post_id", post)
            .addFormDataPart("text", psts.random().take(900))//"Няшулий,+прмсоединяйся+к+няшному+чату+няшулиев ")
            .addFormDataPart("captcha_key", "comment")
            .addFormDataPart("captcha", solveCaptcha(keyPHPSESSID.first.first, keyPHPSESSID.first.second, proxyIp, proxyPort, is_com = true))
            .build()

        val request = Request.Builder()
            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; rv:78.0) Gecko/20100101 Firefox/78.0")
            .header("Referer", "https://1chan.top/news/")
            .header("Cookie", "PHPSESSID=${keyPHPSESSID.first.second}; key=${keyPHPSESSID.first.first}; homeboard=krautchan.net")
            .url("https://1chan.top/news/res/$post/add_comment/")
            .post(requestBody)
            .build()

        OkHttpClient().newBuilder().proxy(proxyWipe).retryOnConnectionFailure(true).build().newCall(request).execute().body!!.close()
    }
}