----------------- ProxyServer.bat 说明 -----------------
1. ProxyServer.bat
    运行ProxyServer.bat, 监听端口: 6666

2. 打开IE, 菜单 - 工具 - Internet 选项 - 连接(选项卡) - 局域网设置 - 代理服务器
    勾上 为 LAN 使用代理服务器 (这些设置不会应用于拨号或 VPN 连接)。
    然后点高级, 服务器:
    类型        要使用的代理服务器地址    端口
    HTTP(H):    127.0.0.1                 6666

    确定

3. 在IE浏览器里打开任意网址即可.

工作流程:
    在IE里面设置好代理之后, 当浏览器访问网页的时候会首先到 127.0.0.1:6666访问, 也就是我们写的代理服务器。
    我们的代理服务器收到请求之后会到真正的服务器去请求数据, 然后再发送给浏览器.

----------------- ProxyProxyServer.bat 说明 -----------------
下面使用了两层代理, 一个是Fiddler, 一个是我们写的这个代理
Fiddler下载地址: http://www.fiddler2.com/fiddler2/

1. 安装Fiddler, 并确保Fiddler可以运行(可能需要安装.net framework)
2. 打开Fiddler, 打开ProxyProxyServer.bat
3. 打开IE浏览器, 访问任意网址

工作流程:
    在IE里面设置好代理之后, 当浏览器访问网页的时候会首先到 127.0.0.1:6666 访问, 也就是我们写的代理服务器。
    我们的代理服务器收到请求之后会到127.0.0.1:8888, 这个是Fiddler监听的端口, Fiddler收到我们的请求之后会到真正的服务器去请求数据,
    然后再发送给我们写的代理服务器, 我们写的代理服务器再发给浏览器.

上面也可以把Fiddler换成我们的代理服务器, 运行Fiddler.bat, 用它来模拟Fiddler
也就是相当于同时运行两个ProxyServer.bat, 参数分别是:

## ProxyProxyServer.bat的参数
com.skin.taurus.proxy.Main -p 6666 -proxyHost 127.0.0.1 -proxyPort 8888

## ProxyServer.bat的参数
com.skin.taurus.proxy.Main -p 8888

浏览器访问 127.0.0.1:6666, 然后 127.0.0.1:6666 访问 127.0.0.1:8888, 然后 127.0.0.1:8888 访问真正的服务器


