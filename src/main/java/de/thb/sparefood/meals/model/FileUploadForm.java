package de.thb.sparefood.meals.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.jboss.resteasy.annotations.providers.multipart.PartType;

import javax.ws.rs.FormParam;
import java.io.File;

@Data
@NoArgsConstructor
public class FileUploadForm {

  @FormParam("file")
  @PartType("application/octet-stream")
  private File data;
}
