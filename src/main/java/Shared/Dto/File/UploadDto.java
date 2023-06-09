package Shared.Dto.File;

import Shared.Enums.UploadType;

public class UploadDto {
    private String name;
    private String memeType;
    private UploadType uploadType;
    private int referenceId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMemeType() {
        return memeType;
    }

    public void setMemeType(String memeType) {
        this.memeType = memeType;
    }

    public UploadType getUploadType() {
        return uploadType;
    }

    public void setUploadType(UploadType uploadType) {
        this.uploadType = uploadType;
    }

    public int getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(int referenceId) {
        this.referenceId = referenceId;
    }
}
