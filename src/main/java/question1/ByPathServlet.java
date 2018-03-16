package question1;


import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_PATHS;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.Writer;
import java.util.*;


@Component(
        service ={Servlet.class},
        property = {
                SLING_SERVLET_PATHS + "=/bypath"
        }
)

public class ByPathServlet extends SlingSafeMethodsServlet {

    private final Logger log = LoggerFactory.getLogger(ByPathServlet.class);


    @Override
    protected void doGet(SlingHttpServletRequest request,
                         SlingHttpServletResponse response) throws ServletException,
            IOException {


        Resource resource =request.getResourceResolver().getResource("/content/test");
        Writer w = response.getWriter();


        if(resource!=null) {
            ValueMap valueMap = resource.adaptTo(ValueMap.class);

            List<Resource> childrenList = new ArrayList<>();

            Iterator<Resource> children = resource.listChildren();


            while (children.hasNext()) {
                childrenList.add(children.next());
            }

            w.write("Before sort \n");
            for (Resource resource1 : childrenList) {
                w.write(resource1.getName() + " "+resource1.getValueMap().get("jcr:created","def")+"\n");
            }

            final String orderChildBy = request.getParameter("orderChildBy");
            Collections.sort(childrenList, new Comparator<Resource>() {
                @Override
                public int compare(Resource o1, Resource o2) {
                    ValueMap proValueMap1 = o1.getValueMap();
                    ValueMap proValueMap2 = o2.getValueMap();
                    String st1 = proValueMap1.get("jcr:created", "default");
                    String st2 = proValueMap2.get("jcr:created", "default");
                    if (orderChildBy.equals("assc"))
                        return st1.compareTo(st2);
                    else if (orderChildBy.equals("desc"))
                        return st2.compareTo(st1);
                    else
                        return st1.compareTo(st2);
                }
            });


           w.write("After sort \n");
            for (Resource resource1 : childrenList) {
                   w.write(resource1.getName() + " "+resource1.getValueMap().get("jcr:created","def")+"\n");
            }
        }

        else{
            w.write("404 Not Found");
        }

    }

}
