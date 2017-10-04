package com.bsmwireless.screens.transfer;

import com.bsmwireless.screens.common.menu.BaseMenuView;

import app.bsmuniversal.com.R;

public interface TransferView extends BaseMenuView {
    enum TransferMethod {
        EMAIL(1),
        WEB(2);

        private int mCode;

        TransferMethod(int code) {
            mCode = code;
        }

        public int getCode() {
            return mCode;
        }
    }

    enum Error {
        INVALID_COMMENT(R.string.edit_event_comment_error),
        VALID_COMMENT(R.string.edit_event_valid_comment),
        TRANSFER_FAILED(R.string.transfer_error);

        private int mStringId;

        Error(int stringId) {
            mStringId = stringId;
        }

        public int getStringId() {
            return mStringId;
        }
    }

    void showError(Error error);
    void showTransferError(Error error);
    void showReportDialog();
    void finish();
}
