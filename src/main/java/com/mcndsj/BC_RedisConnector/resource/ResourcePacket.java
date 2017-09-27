package com.mcndsj.BC_RedisConnector.resource;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.connection.DownstreamBridge;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.PacketWrapper;

import java.beans.ConstructorProperties;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.logging.Level;

/**
 * Created by Phoenix616 on 24.03.2015.
 */
public class ResourcePacket extends DefinedPacket {

    private String url;
    private String hash;

    public ResourcePacket() {};

    @ConstructorProperties({"url", "hash"})
    public ResourcePacket(String url, String hash) {
        this.url = url;
        if(hash != null) {
            this.hash = hash.toLowerCase();
        } else {
            this.hash = Hashing.sha1().hashString(url, Charsets.UTF_8).toString().toLowerCase();
        }
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception {
        if(handler instanceof DownstreamBridge) {
            DownstreamBridge bridge = (DownstreamBridge) handler;
            try {
                Field con = bridge.getClass().getDeclaredField("con");
                con.setAccessible(true);
                try {
                    UserConnection usercon = (UserConnection) con.get(bridge);
                    relayPacket(usercon, new PacketWrapper(this, Unpooled.copiedBuffer(ByteBuffer.allocate(Integer.toString(this.getUrl().length()).length()))));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        } else {
            throw new UnsupportedOperationException("Only players can receive ResourcePackSend packets!");
        }
    }

    @Override
    public boolean equals(Object o) {
        if(o == this) {
            return true;
        } else if(o instanceof ResourcePacket) {
            ResourcePacket other = (ResourcePacket)o;
            String this$url = this.getUrl();
            String other$url = other.getUrl();
            if(this$url == null && other$url == null) {
                return true;
            }
            if(this$url == null || other$url == null) {
                return false;
            }
            if(!this$url.equals(other$url)) {
                return false;
            }
            String this$hash = this.getHash();
            String other$hash = other.getHash();

            if(this$hash == null && other$hash == null) {
                return true;
            }
            if(this$hash == null || other$hash == null) {
                return false;
            }
            return this$hash.equals(other$hash);
        }
        return false;
    }

    public void relayPacket(UserConnection usercon, PacketWrapper packet) throws Exception {
        usercon.getPendingConnection().handle(packet);
    }

    public void read(ByteBuf buf) {
        this.url = readString(buf);
        this.hash = readString(buf);
    }

    public void write(ByteBuf buf) {
        writeString(this.url, buf);
        writeString(this.hash, buf);
    }

    public String getUrl() {
        return this.url;
    }

    public String getHash() {
        return this.hash;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setHash(String hash) {
        if(hash != null) {
            this.hash = hash.substring(0, 39).toLowerCase();
        } else {
            this.hash = Hashing.sha1().hashString(this.getUrl(), Charsets.UTF_8).toString().substring(0, 39).toLowerCase();
        }
    }

    public String toString() {
        return "ResourcePackSend(url=" + this.getUrl() + ", hash=" + this.getHash() + ")";
    }

    public int hashCode() {
        int result = 1;
        String $url = this.getUrl();
        result = result * 59 + ($url == null?0:$url.hashCode());
        String $hash = this.getHash();
        result = result * 59 + ($hash == null?0:$hash.hashCode());
        return result;
    }
}
