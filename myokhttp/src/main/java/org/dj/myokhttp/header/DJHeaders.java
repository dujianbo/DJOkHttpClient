package org.dj.myokhttp.header;

/**
 * 作者：DuJianBo on 2017/11/28 13:12
 * 邮箱：jianbo_du@foxmail.com
 */

public interface DJHeaders {

    String MALL = "mall";
    String SCP = "scp";

    /**
     * 公共header
     */
    String HEADER_TYPE_HEADER = "customHeader";

    String HEADER_MALL = HEADER_TYPE_HEADER + ":" + MALL;

    String HEADER_SCP = HEADER_TYPE_HEADER + ":" + SCP;

    /**
     * 是否加密
     */
    String HEADER_TYPE_ENCRYPT = "encrypt";

    String HEADER_MALL_ENCRYPT = HEADER_TYPE_ENCRYPT + ":" + MALL;

    String HEADER_SCP_ENCRYPT = HEADER_TYPE_ENCRYPT + ":" + SCP;

    /**
     * 公共URL参数
     */
    String HEADER_TYPE_URL_PARAM = "url_param";

    String HEADER_MALL_URL_PARAM = HEADER_TYPE_URL_PARAM + ":" + MALL;

    String HEADER_SCP_URL_PARAM = HEADER_TYPE_URL_PARAM + ":" + SCP;

    /**
     * 公共BODY参数
     */
    String HEADER_TYPE_BODY_PARAM = "body_param";

    String HEADER_MALL_BODY_PARAM = HEADER_TYPE_BODY_PARAM + ":" + MALL;

    String HEADER_SCP_BODY_PARAM = HEADER_TYPE_BODY_PARAM + ":" + SCP;
}
