package webshop.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.*;
import webshop.api.Utils;
import webshop.api.model.Image;
import webshop.api.repository.ImageRepository;

import java.io.*;
import java.util.Base64;
import java.util.Optional;

@RestController
@RequestMapping("/img")
public class ImageController {
		@Autowired
		private ImageRepository repository;

		public ResponseEntity<Object> createOrUpdate(String name, byte[] data) {
				Image in = new Image();
				in.name = name;
				in.data = data;
				Image out = repository.save(in);
				return ResponseEntity.ok(name);
		}

		/* (C)RUD */
		@PostMapping("/{name}")
		public ResponseEntity<Object> create(@PathVariable String name, @RequestBody String base64) throws IOException {
				byte[] data = Base64.getDecoder().decode(base64);
				try {
						final File outputFile = new File("C:\\Temp-NVME\\SpringDebug\\" + name);
						if (outputFile.exists()) {
								outputFile.delete();
						}
						outputFile.createNewFile();
						try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
								outputStream.write(data);
						}
				} catch (Exception e) { }

				return createOrUpdate(name, data);
		}

		/* C(R)UD */
		@GetMapping(value = "/{name}")
		public ResponseEntity<Object> read(@PathVariable String name) {
				Optional<Image> optional = repository.findById(name);
				if (optional.isPresent()) {
						Image image = optional.get();
						MimeType mimeType = Utils.mimeTypeFromFileName(image.name);
						System.out.println("media type for '" + image.name + "\" = " + mimeType.toString());
						final ByteArrayResource inputStream = new ByteArrayResource(image.data);
						return ResponseEntity
										.status(HttpStatus.OK)
										.contentType(MediaType.asMediaType(mimeType))
										.contentLength(inputStream.contentLength()).body(inputStream);
				} else {
						return Utils.notFound("image", name);
				}
		}

		/* CR(U)D */
		@PutMapping("/{name}")
		public ResponseEntity<Object> update(@PathVariable String name, @RequestBody String base64) throws IOException {
				byte[] data = Base64.getDecoder().decode(base64);
				try {
						final File outputFile = new File("C:\\Temp-NVME\\SpringDebug\\" + name);
						if (outputFile.exists()) {
								outputFile.delete();
						}
						outputFile.createNewFile();
						try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
								outputStream.write(data);
						}
				} catch (Exception e) { }

				return createOrUpdate(name, data);
		}

		/* CRU(D) */
		@DeleteMapping("/{id}")
		public ResponseEntity<Object> delete(@PathVariable String id) {
				Optional<Image> optional = repository.findById(id);
				if (optional.isPresent()) {
						Image image = optional.get();
						repository.delete(image);
						return ResponseEntity.ok(image);
				} else {
						return Utils.notFound("Image", id);
				}
		}
}
