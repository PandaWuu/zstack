package org.zstack.image;

import org.zstack.header.image.ImageVO;
import org.zstack.header.tag.TagDefinition;
import org.zstack.tag.PatternedSystemTag;

/**
 * Created by xing5 on 2016/7/18.
 */
@TagDefinition
public class ImageSystemTags {
    public static String IMAGE_NAME_TOKEN = "imageName";
    public static String PRIMARY_STORAGE_UUID_TOKEN = "primaryStorageUuid";
    public static String IMAGE_INJECT_QEMUGA_TOKEN = "qemuga";
    public static PatternedSystemTag IMAGE_INJECT_QEMUGA = new PatternedSystemTag(String.format("%s", IMAGE_INJECT_QEMUGA_TOKEN), ImageVO.class);
    public static PatternedSystemTag DELETED_IMAGE_CACHE = new PatternedSystemTag(
            String.format("imageName::{%s}::primaryStorageUuid::{%s}", IMAGE_NAME_TOKEN, PRIMARY_STORAGE_UUID_TOKEN),
            ImageVO.class
    );

    public static String IMAGE_DEPLOY_REMOTE_TOKEN = "remote";
    public static PatternedSystemTag IMAGE_DEPLOY_REMOTE = new PatternedSystemTag(String.format("%s", IMAGE_DEPLOY_REMOTE_TOKEN), ImageVO.class);

    public static String IMAGE_SEVER_CERT_TOKEN = "imageCert";
    public static PatternedSystemTag IMAGE_SERVER_CERT = new PatternedSystemTag(
            String.format("image::cert::{%s}", IMAGE_SEVER_CERT_TOKEN),
            ImageVO.class
    );

    public static String IMAGE_SOURCE_TYPE_TOKEN = "sourceType";
    public static PatternedSystemTag IMAGE_SOURCE_TYPE = new PatternedSystemTag(String.format("sourceType::{%s}", IMAGE_SOURCE_TYPE_TOKEN), ImageVO.class);
}
