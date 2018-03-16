package question2;



    import com.itextpdf.text.Document;
        import com.itextpdf.text.DocumentException;
        import com.itextpdf.text.Paragraph;
        import com.itextpdf.text.pdf.PdfWriter;
        import org.apache.sling.api.SlingHttpServletRequest;
        import org.apache.sling.api.SlingHttpServletResponse;
        import org.apache.sling.api.resource.Resource;
        import org.apache.sling.api.resource.ValueMap;
        import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
        import org.osgi.service.component.annotations.Component;
        import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;

        import javax.servlet.Servlet;
        import javax.servlet.ServletException;
        import javax.servlet.ServletOutputStream;
        import java.io.IOException;
    import java.io.Writer;
    import java.util.*;

        import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES;


@Component(service = {Servlet.class}, property = {SLING_SERVLET_RESOURCE_TYPES + "=restype"})
@SuppressWarnings("serial")
public class question2PDFformat  extends SlingSafeMethodsServlet {


    private final Logger log = LoggerFactory.getLogger(question2PDFformat.class);

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws ServletException, IOException {

        Resource resource = request.getResource();
        Iterator<Resource> children = resource.listChildren();

        List<Resource> childrenList = new ArrayList();
        final String order = request.getParameter("order");

        log.error("order: {}", order);

         if(resource!=null && order!=null) {

            Document document = new Document();

            ServletOutputStream baos = response.getOutputStream();
            response.setContentType("application/pdf");


            try {

                PdfWriter.getInstance(document, baos);
                document.open();


                while (children.hasNext()) {
                    childrenList.add(children.next());
                }

                document.add(new Paragraph("Unordered List"));
                for (Resource resource1 : childrenList) {
                    document.add(new Paragraph(resource1.getName() + "             " + resource1.getValueMap().get("jcr:created", "default")));
                }




                Collections.sort(childrenList, new Comparator<Resource>() {
                    @Override
                    public int compare(Resource o1, Resource o2) {
                        ValueMap proValueMap1 = o1.getValueMap();
                        ValueMap proValueMap2 = o2.getValueMap();
                        String st1 = proValueMap1.get("jcr:created", "default");
                        String st2 = proValueMap2.get("jcr:created", "default");
                        if (order.equals("assc"))
                            return st1.compareTo(st2);
                        else if (order.equals("desc"))
                            return st2.compareTo(st1);
                        else
                            return st1.compareTo(st2);
                    }
                });

                document.add(new Paragraph("\n"));

                document.add(new Paragraph("Ordered List " + order));
                for (Resource resource1 : childrenList) {
                    document.add(new Paragraph(resource1.getName() + "            " + resource1.getValueMap().get("jcr:created", "default")));
                }

                document.close();
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }
        else{
            Writer w=response.getWriter();
            w.write("404 Not Found");
        }

}

}