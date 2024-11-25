package demo.config.types;

import demo.model.Resource;
import framework.annotations.Injectable;
import framework.configurables.Stringifier;
import framework.http.responseentity.ContentType;

@Injectable
public class ResourceToHtmlStringifier implements Stringifier {
    @Override
    public String stringify(Object o) {
        if (o instanceof Resource resource) {
            return String.format("""
            <html>
                <head>
                    <meta charset="UTF-8">
                    <title>%s</title>
                </head>
                <body>
                    <h1>%s</h1>
                    <p>Reference name: %s</p>
                    <p>Price: €%6.2f</p>
                </body>
            </html>
           
            """, resource.name(), resource.name(), resource.referenceName(), resource.price());
        }

        if(o==null) return "<html><head><title>Not Found</title></head><body><h1>Object not found</h1></body></html>";

        return String.format("""
                <html>
                    <head>
                        <title>Cast Error</title>
                    </head>
                    <body>
                        <h1>Object is not a resource</h1>
                        <p>Object: %s</p>
                        <p>Class: %s</p>
                    </body>
                </html>""", o, o.getClass());
    }

    @Override
    public ContentType getContentType() {
        return ContentType.HTML;
    }
}
