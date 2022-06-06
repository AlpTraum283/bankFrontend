package org.example.views;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinServletRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.example.views.request.RequestView;
import org.example.views.transfer.TransferRequestView;
import org.example.views.user.UserInfoView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import static org.example.Constants.jwtSecret;

/**
 * The main view contains a button and a click listener.
 */
@Route(value = "")
public class MainView extends AppLayout implements BeforeEnterObserver {

    public MainView() {

        createHeader();
        createDrawer();
    }

    private void createHeader() {

        HorizontalLayout header = new HorizontalLayout(
                new DrawerToggle()

        );
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidth("100%");
        header.addClassNames("py-0", "px-m");
        addToNavbar(header);
    }

    private void createDrawer() {
        RouterLink profile = new RouterLink("Profile", UserInfoView.class);
        RouterLink transfer = new RouterLink("Transfer", TransferRequestView.class);
        RouterLink request = new RouterLink("Request", RequestView.class);

        profile.setHighlightCondition(HighlightConditions.sameLocation());
        transfer.setHighlightCondition(HighlightConditions.sameLocation());
        request.setHighlightCondition(HighlightConditions.sameLocation());
        addToDrawer(new VerticalLayout(

                profile,
                transfer,
                request
        ));
    }

    public static String getUserIdFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        String token = (String) ComponentUtil.getData(UI.getCurrent(), "token");
        HttpServletRequest request = VaadinServletRequest.getCurrent().getHttpServletRequest();
        Cookie[] cookies = request.getCookies();
        String cookieToken = null;
        for (Cookie cookie : cookies) {
            if ("token".equals(cookie.getName())) {
                cookieToken = cookie.getValue();
            }
        }
        if (cookieToken == null) {
            if (token == null
                    || "".equals(token)) {
                beforeEnterEvent.forwardTo(LoginView.class);
            }
        }

    }
}
