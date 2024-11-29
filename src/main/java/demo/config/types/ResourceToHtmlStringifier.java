package demo.config.types;

import demo.model.Resource;
import framework.annotations.Injectable;
import framework.configurables.Stringifier;
import framework.request.response.ContentType;

@Injectable
public class ResourceToHtmlStringifier implements Stringifier {
    @Override
    public String stringify(Object o) {
        if (o==null)
            return HtmlHelper.notFound();

        if (o instanceof Resource resource) {
            String details = String.format("""
            <p>Reference name: %s</p>
            <p>Price: €%6.2f</p>
            """, resource.referenceName(), resource.price());

            return HtmlHelper.createHtmlString(resource.name(), resource.name(), details);
        }

        return HtmlHelper.wrongClass(Resource.class, o);
    }

    @Override
    public ContentType getContentType() {
        return ContentType.HTML;
    }
}
