/**
 * Title
 *
 * @ClassName: controller
 * @Description:
 * @author: 巫宗霖
 * @date: 2023/3/9 20:38
 * @Blog: https://www.wzl1.top/
 */

package cn.katool.orderservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/")
public class controller {
    @GetMapping("/100")
    public String getMain(){
        return "123";
    }
}
