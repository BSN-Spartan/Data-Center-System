package com.spartan.dc.controller.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

/**
 * @author Dell
 */
@Controller
@RequestMapping("/code")
@ApiIgnore
public class CodeController {

    //
    private final static Logger logger = LoggerFactory.getLogger(CodeController.class);

    // image width
    private static final int WIDTH = 168;

    // image height
    private static final int HEIGHT = 38;

    //
    private static final int CODE_COUNT = 6;

    private static final int XX = 23;
    private static final int FONT_HEIGHT = 25;
    private static final int CODE_Y = 25;
    private static final char[] CODE_SEQUENCE = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j',
            'k', 'm', 'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
            'x', 'y', 'z', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M',
            'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};



    @GetMapping("getCode")
    public void getCaptcha(HttpServletRequest req, HttpServletResponse resp) {
        logger.info("Generate verification code......start.......");
        try {


            BufferedImage buffImg = new BufferedImage(WIDTH, HEIGHT,
                    BufferedImage.TYPE_INT_RGB);
            Graphics gd = buffImg.getGraphics();

            Random random = new Random();

            gd.setColor(Color.WHITE);
            gd.fillRect(0, 0, WIDTH, HEIGHT);


            Font font = new Font("Fixedsys", Font.BOLD, FONT_HEIGHT);

            gd.setFont(font);


            gd.setColor(Color.BLACK);
            gd.drawRect(0, 0, WIDTH - 1, HEIGHT - 1);


            gd.setColor(Color.BLACK);
            for (int i = 0; i < 20; i++) {
                int x = random.nextInt(WIDTH);
                int y = random.nextInt(HEIGHT);
                int xl = random.nextInt(12);
                int yl = random.nextInt(12);
                gd.drawLine(x, y, x + xl, y + yl);
            }


            StringBuffer randomCode = new StringBuffer();
            int red = 0, green = 0, blue = 0;


            for (int i = 0; i < CODE_COUNT; i++) {

                String code = String.valueOf(CODE_SEQUENCE[random.nextInt(CODE_SEQUENCE.length - 1)]);

                gd.setColor(new Color(red, green, blue));
                gd.drawString(code, (i + 1) * XX, CODE_Y);


                randomCode.append(code);
            }

            HttpSession session = req.getSession();
            session.setAttribute("code", randomCode.toString());
            logger.info("Generate verification code......【{}】.......", randomCode.toString());


            resp.setHeader("Pragma", "no-cache");
            resp.setHeader("Cache-Control", "no-cache");
            resp.setDateHeader("Expires", 0);

            resp.setContentType("image/jpeg");


            ServletOutputStream sos = resp.getOutputStream();
            ImageIO.write(buffImg, "jpeg", sos);
            sos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("Generate verification code......end.......");
    }
}
