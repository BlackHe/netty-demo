package com.peony.webSocket;

import com.alibaba.fastjson.JSONObject;
import com.peony.bean.ChannelClientMapping;
import com.peony.bean.ClientType;
import com.peony.bean.MessageMode;
import com.peony.bean.OnlineClientMapping;
import com.peony.message.MessageHandler;
import com.peony.message.MessageHandlerSeletor;
import com.peony.utils.Log;
import com.peony.utils.MessageUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;

import java.io.UnsupportedEncodingException;


public class WebSocketServerHandler extends SimpleChannelInboundHandler<Object> {


    /**
     * 握手对象
     */
    private WebSocketServerHandshaker handshaker;


    /**
     * 1
     * Gets called after the ChannelHandler was added to the actual context and it's ready to handle events.
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress());
        System.out.println("handler added");
    }

    /**
     * 2
     * The Channel of the ChannelHandlerContext was registered with its EventLoop
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel registered");
    }


    /**
     * 3
     * The Channel of the ChannelHandlerContext is now active
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel active");
    }


    /**
     * 4
     * 请求处理
     * @param ctx
     * @param obj
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object obj) throws Exception {
        System.out.println("channel read0");
        System.out.println(obj);
        System.out.println(obj.getClass().getName());
        //处理将要升级为websocket协议签到的http握手请求
        if (obj instanceof FullHttpRequest){
            //需要验证是否为websocket
            FullHttpRequest httpRequest = (FullHttpRequest)obj;
            Log.log("基于http的握手请求");
            Log.log("[客户端请求路径]"+httpRequest.uri().toString());
            handlerHttpRequest(ctx,httpRequest);
        //webSocket消息帧处理
        }else if (obj instanceof WebSocketFrame){
            //websocket接入
            log("基于webSocket的连接会话");
            handlerWebSocketFrame(ctx,(WebSocketFrame)obj);
        }

    }


    /**
     * 5
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel read complete");
        ctx.flush();
    }

    /**
     * 6
     * The Channel of the ChannelHandlerContext was registered is now inactive and reached its end of lifetime.
     * 刷新页面，关闭页签，关闭浏览器等，会先调用此方法，再调用handlerRemoved
     * 相当于掉线
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel inActive");

    }


    /**
     * 7
     * The Channel of the ChannelHandlerContext was unregistered from its EventLoop
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel unRegistered");
    }

    /**
     * 8
     * Gets called after the ChannelHandler was removed from the actual context and it doesn't handle events anymore.
     * 刷新页面，关闭页签，关闭浏览器等，会先调用此方法，再调用handlerRemoved
     * 相当于退出
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("handler removed");
        OffLineHandler.removeClient(ctx);
    }


    /**
     * 9
     * Gets called if a Throwable was thrown.
     * 出现异常时会被调用
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("exception caught");
        cause.printStackTrace();
        ctx.close();
        //一旦出现异常，删除在线状态信息
        OffLineHandler.removeClient(ctx);
    }






    /**
     * 处理websocket请求
     * @param ctx
     * @param frame
     */
    public void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) throws UnsupportedEncodingException {
        //判断是否是关闭链路的指令
        if (frame instanceof CloseWebSocketFrame){
//            handshaker.close(ctx.channel(),((CloseWebSocketFrame) frame).retain());
            return;
        }
        //判断是否是ping消息
        if (frame instanceof PingWebSocketFrame){
            ctx.channel().writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        //判断是否是文本消息
        if (! (frame instanceof TextWebSocketFrame)){
//            throw new UnsupportedOperationException(frame.getClass().getName());
            ctx.channel().writeAndFlush(new String("不支持的消息类型!".getBytes(),"utf-8"));
            return;
        }
        //返回应答消息
        String request = ((TextWebSocketFrame) frame).text();
        System.out.println("客户端消息："+request);
        //解析json的消息
        MessageMode msgObj;
        try{
            msgObj = JSONObject.parseObject(request,MessageMode.class);
        }catch (Exception e){
            ctx.channel().writeAndFlush(new TextWebSocketFrame("消息格式不合法，请检查!"));
            return;
        }
        if (!MessageUtil.verify(msgObj)){
            ctx.channel().writeAndFlush(new TextWebSocketFrame("消息格式不合法，请检查!"));
            return;
        }
        //选择合适的消息处理器，响应客户端
        MessageHandler messageHandler = MessageHandlerSeletor.get(msgObj);
        if (messageHandler == null){
            ctx.channel().writeAndFlush(new TextWebSocketFrame("消息格式不合法，请检查!"));
            return;
        }
        messageHandler.handlerMessage(ctx,msgObj);
    }



    /**
     * 处理http握手请求
     */
    public void handlerHttpRequest(ChannelHandlerContext ctx,FullHttpRequest req){
        if (!req.decoderResult().isSuccess() || !"websocket".equalsIgnoreCase(req.headers().get("Upgrade"))){
            sendHttpResponse(ctx,req,new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.BAD_REQUEST));
            return;
        }
        //握手之前，判断uri是否合法，是则缓存客户端id和channel
        String uri = req.uri();
        if (uri == null || uri.isEmpty()){
            sendHttpResponse(ctx,req,new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.BAD_REQUEST));
            return;
        }
        String[] uriArgs = uri.split("/");
        if (uriArgs.length < 3){
            sendHttpResponse(ctx,req,new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.BAD_REQUEST));
            return;
        }
        String clientType = uriArgs[2];
        String clientId = uriArgs[3];
        //uri eg -->   ws://localhost:8080/ws/cs/987329323
        //握手前先判断该客户端是否已在线
        if (isOnline(clientType,clientId)){
            sendHttpResponse(ctx,req,new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.BAD_REQUEST));
            return;
        }
        //分开缓存客服和用户的clientId和channelId映射关系
        if (ClientType.CUSTOMER_SERVICE.equals(clientType)){
            OnlineClientMapping.get().customServiceMap.put(clientId,ctx.channel());
        }else if (ClientType.USER.equals(clientType)){
            OnlineClientMapping.get().userMap.put(clientId,ctx.channel());
        }
        //缓存全部链接用户的channelId和client的映射关系
        ChannelClientMapping.get().map.put(ctx.channel().id().asShortText(),uriArgs[3]);
        //构造握手响应返回
        WebSocketServerHandshakerFactory handshakerFactory = new WebSocketServerHandshakerFactory("ws://localhost:8080/ws", null, false);
        handshaker = handshakerFactory.newHandshaker(req);
        if (handshaker == null){
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
        }else {
            handshaker.handshake(ctx.channel(),req);
        }
        Log.log("与客户端["+ctx.channel().remoteAddress()+"]握手成功");
    }

    public void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest request, FullHttpResponse response){
        if (response.status().code() != 200){
            ByteBuf byteBuf = Unpooled.copiedBuffer(response.status().toString(), CharsetUtil.UTF_8);
            response.content().writeBytes(byteBuf);
            byteBuf.release();
            response.headers().set("Content-Length",byteBuf.capacity()+"");
        }
        //如果是非 Keep-Alive,关闭连接
        ChannelFuture channelFuture = ctx.channel().writeAndFlush(response);
        if (!request.headers().get("Connection").equals("Keep-Alive") || response.status().code() != 200){
            channelFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }


    private boolean isOnline(String clientType, String clientId){
        if (ClientType.CUSTOMER_SERVICE.equals(clientType)){
            return OnlineClientMapping.get().customServiceMap.containsKey(clientId);
        }else if (ClientType.USER.equals(clientType)){
            return OnlineClientMapping.get().userMap.containsKey(clientId);
        }else {
            return false;
        }
    }


    public static void log(String str) {
        System.out.println(str);
    }




}
