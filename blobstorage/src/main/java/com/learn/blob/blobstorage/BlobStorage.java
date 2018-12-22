package com.learn.blob.blobstorage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.microsoft.azure.storage.blob.BlobURL;
import com.microsoft.azure.storage.blob.BlockBlobURL;
import com.microsoft.azure.storage.blob.ContainerURL;
import com.microsoft.azure.storage.blob.Metadata;
import com.microsoft.azure.storage.blob.PipelineOptions;
import com.microsoft.azure.storage.blob.ServiceURL;
import com.microsoft.azure.storage.blob.SharedKeyCredentials;
import com.microsoft.azure.storage.blob.StorageURL;
import com.microsoft.azure.storage.blob.models.BlockBlobUploadResponse;
import com.microsoft.rest.v2.http.HttpPipeline;

import io.reactivex.Flowable;
import io.reactivex.Single;

@RestController
public class BlobStorage {
	private static final String ACCOUT= "<Your ACCOUNT>";
	private static final String URL ="https://<Your ACCOUNT>.blob.core.windows.net";
	private static final String KEY="<Shared Key>";
	
	@PostMapping(value="/api/upload")
	@ResponseStatus(code = HttpStatus.OK)
	public Integer uploadFile(@RequestBody MultipartFile file) throws InvalidKeyException, IOException {
		Integer responseCode = null;
		SharedKeyCredentials credentials = new SharedKeyCredentials(ACCOUT, KEY);
		HttpPipeline pipeline = StorageURL.createPipeline(credentials,new PipelineOptions());
		ServiceURL serviceURL = new ServiceURL(new java.net.URL(URL), pipeline);
		ContainerURL containerURL = serviceURL.createContainerURL("container1");
		BlobURL blobUrl = containerURL.createBlobURL(file.getOriginalFilename());
		Metadata metadata = new Metadata();
		metadata.put("individualId", "1233");
		metadata.put("appointmentId", "678");
		metadata.put("dateAndTime","12-21-2018");
		blobUrl.setMetadata(metadata,null,null);
		BlockBlobURL blockBlob = new BlockBlobURL(new java.net.URL(blobUrl.toString()), pipeline);
		
		Single<BlockBlobUploadResponse> response = blockBlob.upload(Flowable.just(ByteBuffer.wrap(file.getBytes())), file.getSize(), null, metadata, null,null);
		response.subscribe(rep -> {
		System.out.println(rep.statusCode());});
		
		return responseCode;
	}
	
	@PostMapping(value="/api/download")
	public String downloadFile() {
		return null;
	}
	
	@PostMapping(value="/api/fileList")
	public String getList() {
		return null;
	}
	
}
