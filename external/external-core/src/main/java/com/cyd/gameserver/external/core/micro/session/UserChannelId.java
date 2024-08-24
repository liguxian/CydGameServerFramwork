package com.cyd.gameserver.external.core.micro.session;

import java.util.Objects;

public record UserChannelId(String channelId) {

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (!(obj instanceof UserChannelId that)) {
            return false;
        }

        return Objects.equals(channelId, that.channelId);
    }

    @Override
    public int hashCode(){
        return Objects.hash(channelId);
    }
}
