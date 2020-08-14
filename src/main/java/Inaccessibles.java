import net.dv8tion.jda.internal.requests.Method;
import net.dv8tion.jda.internal.requests.Route;

import java.lang.reflect.Constructor;

public class Inaccessibles {
    /**
     * This is private by default
     *
     * @param method look
     * @param route  somewhere
     * @return       else
     */
    public static Route getRoute(Method method, String route) {
        try {
            Constructor<?> constructor = Route.class.getDeclaredConstructor(Method.class, String.class);
            constructor.setAccessible(true);
            return (Route) constructor.newInstance(method, route);
        } catch (Exception e) {
            return null;
        }
    }

    public static String toUnsignedString(long num) {
        return Long.toUnsignedString(num);
    }
}
