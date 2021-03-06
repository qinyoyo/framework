package top.iotequ.framework.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import top.iotequ.framework.exception.IotequException;
import top.iotequ.framework.exception.IotequThrowable;

public class ZxingCode {
	private static final int COLOR_BLACK = 0xFF000000; // 默认是黑色
	private static final int COLOR_WHITE = 0xFFFFFFFF; // 背景颜色	
	private int qrColor;
	private int bkColor;
	public ZxingCode() {
		qrColor=COLOR_BLACK;
		bkColor=COLOR_WHITE;
	}
	public ZxingCode(int frontColor,int backColor) {
		qrColor=frontColor;
		bkColor=backColor;
	}
	
	/**
	 * 生成带logo的二维码图片
	 * @param content  二维码内容
	 * @param width 宽度
	 * @param height 高度
	 * @param logoPic  logo文件,null则生成不带logo的二维码
	 * @param frontColor 前景色
	 * @param backColor 背景色
	 * @return 二维码图像的base64格式
	 * @throws IotequException 失败
	 */
    public static String getQRCode(String content,int width,int height,File logoPic,int frontColor,int backColor) throws IotequException
    {
        if (content==null) return null;
        if ( width<=0 || height<=0) throw new IotequException(IotequThrowable.	PARAMETER_ERROR,"尺寸大小参数不正确");    
    	ZxingCode zp = new ZxingCode(frontColor,backColor);
    	try {
            BufferedImage bim = zp.getQR_CODEBufferedImage(content, BarcodeFormat.QR_CODE, width, height, zp.getDecodeHintType());
            if (logoPic != null && logoPic.exists())
                bim = zp.addLogo_QRCode(bim, logoPic, new LogoConfig(), "");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.flush();
            ImageIO.write(bim, "png", baos);
            String imageBase64QRCode = Base64.encodeBase64String(baos.toByteArray());
            baos.close();
            return imageBase64QRCode;
        }catch (Exception e) {
            throw IotequException.newInstance(e);
        }
    }
	/**
	 * 生成带logo的二维码图片
	 * @param content  二维码内容
	 * @param width 宽度
	 * @param height 高度
	 * @param logoPic  logo文件，null则生成不带logo的二维码
	 * @return 二维码图像
	 * @throws IotequException 失败
	 */
    public static String getQRCode(String content,int width,int height,File logoPic) throws IotequException {
    	return getQRCode(content, width, height,logoPic,COLOR_BLACK,COLOR_WHITE);
    }
	/**
	 * 生成带logo的二维码图片
	 * @param content  二维码内容
	 * @param width 宽度
	 * @param height 高度
	 * @return 二维码图像
	 * @throws IotequException 失败
	 */
    public static String getQRCode(String content,int width,int height) throws IotequException {
    	return getQRCode(content, width, height,null,COLOR_BLACK,COLOR_WHITE);
    }
	/**
	 * 生成带logo的二维码图片
	 * @param content  二维码内容
	 * @param size 宽度
	 * @return 二维码图像
	 * @throws IotequException 失败
	 */
    public static String getQRCode(String content,int size) throws IotequException {
    	return getQRCode(content, size, size,null,COLOR_BLACK,COLOR_WHITE);
    }
    private BufferedImage addLogo_QRCode(BufferedImage bim, File logoPic, LogoConfig logoConfig, String productName) 
    {        
        try {    
        	BufferedImage image = bim;
            Graphics2D g = image.createGraphics();    
            BufferedImage logo = ImageIO.read(logoPic);
            int widthLogo = logo.getWidth(null)>image.getWidth()*3/10?(image.getWidth()*3/10):logo.getWidth(null), 
                heightLogo = logo.getHeight(null)>image.getHeight()*3/10?(image.getHeight()*3/10):logo.getWidth(null);
        
             int x = (image.getWidth() - widthLogo) / 2;
             int y = (image.getHeight() - heightLogo) / 2;
            g.drawImage(logo, x, y, widthLogo, heightLogo, null);
            g.dispose();

            if (productName != null && !productName.equals("")) {
                //新的图片，把带logo的二维码下面加上文字
                BufferedImage outImage = new BufferedImage(400, 445, BufferedImage.TYPE_4BYTE_ABGR);
                Graphics2D outg = outImage.createGraphics();
                //画二维码到新的面板
                outg.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
                //画文字到新的面板
                outg.setColor(Color.BLACK); 
                outg.setFont(new Font("宋体",Font.BOLD,30)); //字体、字型、字号 
                int strWidth = outg.getFontMetrics().stringWidth(productName);
                if (strWidth > 399) {
                    String productName1 = productName.substring(0, productName.length()/2);
                    String productName2 = productName.substring(productName.length()/2, productName.length());
                    int strWidth1 = outg.getFontMetrics().stringWidth(productName1);
                    int strWidth2 = outg.getFontMetrics().stringWidth(productName2);
                    outg.drawString(productName1, 200  - strWidth1/2, image.getHeight() + (outImage.getHeight() - image.getHeight())/2 + 12 );
                    BufferedImage outImage2 = new BufferedImage(400, 485, BufferedImage.TYPE_4BYTE_ABGR);
                    Graphics2D outg2 = outImage2.createGraphics();
                    outg2.drawImage(outImage, 0, 0, outImage.getWidth(), outImage.getHeight(), null);
                    outg2.setColor(Color.BLACK); 
                    outg2.setFont(new Font("宋体",Font.BOLD,30)); //字体、字型、字号 
                    outg2.drawString(productName2, 200  - strWidth2/2, outImage.getHeight() + (outImage2.getHeight() - outImage.getHeight())/2 + 5 );
                    outg2.dispose(); 
                    outImage2.flush();
                    outImage = outImage2;
                }else {
                    outg.drawString(productName, 200  - strWidth/2 , image.getHeight() + (outImage.getHeight() - image.getHeight())/2 + 12 ); //画文字 
                }
                outg.dispose(); 
                outImage.flush();
                image = outImage;
            }
            logo.flush();
            image.flush();
            return image;
        }catch (Exception e) {
        	return bim;
        }
    }
    /**
     * 生成二维码bufferedImage图片
     *
     * @param content
     *            编码内容
     * @param barcodeFormat
     *            编码类型
     * @param width
     *            图片宽度
     * @param height
     *            图片高度
     * @param hints
     *            设置参数
     * @return
     */
    private BufferedImage getQR_CODEBufferedImage(String content, BarcodeFormat barcodeFormat, int width, int height, Map<EncodeHintType, ?> hints) throws Exception
    {
        MultiFormatWriter multiFormatWriter = null;
        BitMatrix bm = null;
        BufferedImage image = null;
        multiFormatWriter = new MultiFormatWriter();
        // 参数顺序分别为：编码内容，编码类型，生成图片宽度，生成图片高度，设置参数
        bm = multiFormatWriter.encode(content, barcodeFormat, width, height, hints);
        int w = bm.getWidth();
        int h = bm.getHeight();
        image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < w; x++)
        {
            for (int y = 0; y < h; y++)
            {
                image.setRGB(x, y, bm.get(x, y) ? qrColor : bkColor);
            }
        }
        return image;
    }

    /**
     * 设置二维码的格式参数
     *
     * @return
     */
    private Map<EncodeHintType, Object> getDecodeHintType()
    {
        // 用于设置QR二维码参数
        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
        // 设置QR二维码的纠错级别（H为最高级别）具体级别信息
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        // 设置编码方式
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MARGIN, 0);
        hints.put(EncodeHintType.MAX_SIZE, 350);
        hints.put(EncodeHintType.MIN_SIZE, 100);

        return hints;
    }

    static class LogoConfig
    {
        // logo默认边框颜色
        public static final Color DEFAULT_BORDERCOLOR = Color.WHITE;
        // logo默认边框宽度
        public static final int DEFAULT_BORDER = 2;
        // logo大小默认为照片的1/5
        public static final int DEFAULT_LOGOPART = 5;

        private final int border = DEFAULT_BORDER;
        private final Color borderColor;
        private final int logoPart;

        /**
         * Creates a default config with on color {@link #BLACK} and off color
         * {@link #WHITE}, generating normal black-on-white barcodes.
         */
        public LogoConfig()
        {
            this(DEFAULT_BORDERCOLOR, DEFAULT_LOGOPART);
        }

        public LogoConfig(Color borderColor, int logoPart)
        {
            this.borderColor = borderColor;
            this.logoPart = logoPart;
        }

        public Color getBorderColor()
        {
            return borderColor;
        }

        public int getBorder()
        {
            return border;
        }

        public int getLogoPart()
        {
            return logoPart;
        }
   }

}
