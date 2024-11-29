package demo.config.types;

public class HtmlHelper {
    public static String createHtmlString(String title, String header, String body) {
        return String.format("""
            <html>
                <head>
                    <meta charset="UTF-8">
                    <title>%s</title>
                </head>
                <body>
                    <h1>%s</h1>
                    %s
                </body>
            </html>
            """, title, header, body);
    }

    public static String notFound(){
        return createHtmlString("Not Found", "Object Not Found", "");
    }

    public static String wrongClass(Class<?> expectedClass, Object object){
        String body = String.format("""
        <p>Expected class: %s</p>
        <p>Actual class: %s</p>
        <p>Object: %s</p>
        """, expectedClass, object.getClass(), object);

        return createHtmlString("Cast Error", "Could not cast to expected type", body);
    }
}
