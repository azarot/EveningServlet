import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;

@WebServlet("/evening")
public class EveningServlet extends HttpServlet {

    private static final String SESSION_ID_NAME = "SESSION_ID_CUSTOM";
    private static final String DEFAULT_NAME = "Buddy";
    private final Map<UUID, Session> nameMap = new HashMap<>();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        UUID sessionId = getSessionId(req);

        if (name != null) {
            nameMap.put(sessionId, new Session(sessionId, name));
        } else {
            name = getNameFromSessionOrDefault(sessionId);
        }

        resp.addCookie(new Cookie(SESSION_ID_NAME, sessionId.toString()));
        try (var writer = resp.getWriter()) {
            writer.printf("Good evening, %s", name);
        }
    }

    private String getNameFromSessionOrDefault(UUID sessionId) {
        Session session = nameMap.get(sessionId);

        if (session != null) {
            Optional<String> storedName = session.getName();
            String name = storedName.orElse(DEFAULT_NAME);
            session.updateTimestampIfValid();

            return name;
        } else {
            return DEFAULT_NAME;
        }
    }

    private UUID getSessionId(HttpServletRequest req) {
        Optional<Cookie> sessionOptional = Arrays.stream(req.getCookies())
                .filter(c -> c.getName().equals(SESSION_ID_NAME))
                .findFirst();

        return sessionOptional
                .map(cookie -> UUID.fromString(cookie.getValue()))
                .orElse(UUID.randomUUID());
    }
}
