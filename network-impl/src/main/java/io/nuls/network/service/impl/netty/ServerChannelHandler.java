package io.nuls.network.service.impl.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.nuls.core.context.NulsContext;
import io.nuls.core.utils.log.Log;
import io.nuls.core.utils.network.IpUtil;
import io.nuls.core.utils.spring.lite.annotation.Autowired;
import io.nuls.network.entity.Node;
import io.nuls.network.service.NetworkService;

import java.nio.ByteBuffer;

/**
 * @author Vive
 */
@ChannelHandler.Sharable
public class ServerChannelHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    private NetworkService networkService;

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();
        String remoteIP = channel.remoteAddress().getHostString();
//        String remoteId = IpUtil.getNodeId(channel.remoteAddress());
//        Node node = getNetworkService().getNode(remoteId);
//        if (node != null) {
//            if (node.getStatus() == Node.CONNECT) {
//                ctx.channel().close();
//                return;
//            }
//            //When nodes try to connect to each other but not connected, select one of the smaller IP addresses as the server
////            if (node.getType() == Node.OUT) {
////                String localIP = InetAddress.getLocalHost().getHostAddress();
////                boolean isLocalServer = IpUtil.judgeIsLocalServer(localIP, remoteIP);
////
////                if (!isLocalServer) {
////                    ctx.channel().close();
////                    return;
////                } else {
////                    getNetworkService().removeNode(remoteId);
////                }
////            }
//        } else {
        // if has a node with same ip, and it's a out node, close this channel
        // if More than 10 in nodes of the same IP, close this channel
        int count = 0;
        for (Node n : getNetworkService().getAvailableNodes()) {
            if (n.getIp().equals(remoteIP)) {
                count++;
                if (count == 10) {
                    ctx.channel().close();
                    return;
                }
//
//                if (n.getType() == Node.OUT && n.getStatus() == Node.HANDSHAKE) {
//                    ctx.channel().close();
//                    return;
//                } else {
//                    count++;
//                    if (count == 10) {
//                        ctx.channel().close();
//                        return;
//                    }
//                }
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //      Log.info("----------------------server channelActive ------------------------- ");
        String channelId = ctx.channel().id().asLongText();
        SocketChannel channel = (SocketChannel) ctx.channel();
        NioChannelMap.add(channelId, channel);
        Node node = new Node(Node.IN, channel.remoteAddress().getHostString(), channel.remoteAddress().getPort(), channelId);
        node.setStatus(Node.CONNECT);
        getNetworkService().addNode(node);
//        getNetworkService().addNodeToGroup(NetworkConstant.NETWORK_NODE_IN_GROUP, node);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //      Log.info("----------------------server channelInactive ------------------------- ");
        SocketChannel channel = (SocketChannel) ctx.channel();
        String channelId = ctx.channel().id().asLongText();
        NioChannelMap.remove(channelId);
        String nodeId = IpUtil.getNodeId(channel.remoteAddress());
        Node node = getNetworkService().getNode(nodeId);
        if (node != null && channelId.equals(node.getChannelId())) {
            getNetworkService().removeNode(nodeId);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        Log.info("---------------ServerChannelHandler exceptionCaught :" + cause.getMessage());
        ctx.channel().close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        SocketChannel channel = (SocketChannel) ctx.channel();
        String nodeId = IpUtil.getNodeId(channel.remoteAddress());
        Node node = getNetworkService().getNode(nodeId);
        if (node != null && node.isAlive()) {
            ByteBuf buf = (ByteBuf) msg;
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            buf.release();
            ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
            buffer.put(bytes);
            getNetworkService().receiveMessage(buffer, node);
        }
    }

    private NetworkService getNetworkService() {
        if (networkService == null) {
            networkService = NulsContext.getServiceBean(NetworkService.class);
        }
        return networkService;
    }
}
