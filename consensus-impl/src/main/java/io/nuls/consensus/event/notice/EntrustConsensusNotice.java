/*
 *
 * MIT License
 *
 * Copyright (c) 2017-2018 nuls.io
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package io.nuls.consensus.event.notice;

import io.nuls.consensus.constant.PocConsensusConstant;
import io.nuls.consensus.entity.tx.PocJoinConsensusTransaction;
import io.nuls.core.constant.ErrorCode;
import io.nuls.core.constant.NulsConstant;
import io.nuls.core.event.BaseEvent;
import io.nuls.core.event.NoticeData;
import io.nuls.core.exception.NulsException;
import io.nuls.core.utils.io.NulsByteBuffer;

/**
 * @author Niels
 * @date 2018/3/8
 */
public class EntrustConsensusNotice extends BaseEvent<PocJoinConsensusTransaction> {
    public EntrustConsensusNotice() {
        super(NulsConstant.MODULE_ID_CONSENSUS, PocConsensusConstant.NOTICE_JOIN_CONSENSUS);
    }

    @Override
    protected PocJoinConsensusTransaction parseEventBody(NulsByteBuffer byteBuffer) throws NulsException {
        return null;
    }

    @Override
    public NoticeData getNotice() {
        NoticeData data = new NoticeData();
        data.setMessage(ErrorCode.JOIN_CONSENSUS);
        data.setData(this.getEventBody().getTxData().getAddress());
        return data;
    }
}
