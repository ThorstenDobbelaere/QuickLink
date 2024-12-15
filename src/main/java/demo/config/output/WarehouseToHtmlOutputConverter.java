package demo.config.output;

import demo.controller.dto.WarehouseDto;
import demo.model.Resource;
import demo.model.Warehouse;
import framework.annotations.Injectable;
import framework.configurables.conversions.OutputConverter;
import framework.request.response.ContentType;

@Injectable
public class WarehouseToHtmlOutputConverter implements OutputConverter {
    @Override
    public String stringify(Object o) {
        if (o==null)
            return HtmlHelper.notFound();

        if (o instanceof WarehouseDto warehouseDto) {

            String details = String.format("""
            <h2>Warehouse</h2>
            <p>Capacity: %6.2f tonnes</p>
            <p>Content: %6.2f tonnes</p>
            <br/>
            <h2>Resource</h2>
            <p>Name: %s</p>
            <p>Price per tonne: €%6.2f</p>
            <br/>
            <h2>Vendor</h2>
            <p>Name: %s</p>
            <p>Address: %s</p>
            """,
                    warehouseDto.capacity(),
                    warehouseDto.content(),
                    warehouseDto.resourceName(),
                    warehouseDto.resourcePrice(),
                    warehouseDto.vendorName(),
                    warehouseDto.vendorAddress());

            String title = String.format("%s %s warehouse", warehouseDto.vendorName(), warehouseDto.resourceName());

            return HtmlHelper.createHtmlString("Warehouse View", title, details);
        }

        return HtmlHelper.wrongClass(Resource.class, o);
    }

    @Override
    public ContentType getContentType() {
        return ContentType.HTML;
    }
}
