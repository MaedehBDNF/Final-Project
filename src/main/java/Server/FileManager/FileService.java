package Server.FileManager;

import Server.Config.DatabaseConfigDto;
import Shared.Dto.File.*;
import Shared.Entities.FileEntity;
import Shared.Enums.Error;
import Shared.Enums.Title;
import Shared.Response;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;

public class FileService {
    private final String resourcesDirectory = System.getProperty("user.dir") + "\\src\\main\\java\\Server\\Resources\\";
    private FileRepository fileRepository;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public FileService(DatabaseConfigDto config, Socket socket) {
        this.fileRepository = new FileRepository(config);
        try {
            InputStream inputStream = socket.getInputStream();
            this.dataInputStream = new DataInputStream(inputStream);
            OutputStream outputStream = socket.getOutputStream();
            this.dataOutputStream = new DataOutputStream(outputStream);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public FileService(DatabaseConfigDto config) {
        this.fileRepository = new FileRepository(config);
    }

    public Response uploadFile(UploadDto uploadDto) {
        String filePath = this.receiveFile(uploadDto);
        return this.uploadFile(uploadDto, filePath);
    }

    public Response uploadFile(UploadDto uploadDto, String filePath) {
        FileEntity fileEntity = new FileEntity();
        Response response = new Response();
        response.setTitle(Title.upload);
        fileEntity.setName(uploadDto.getName());
        fileEntity.setMemeType(uploadDto.getMemeType());
        fileEntity.setPath(filePath);
        try {
            FileDto fileDto = new FileDto();
            int fileId = this.fileRepository.insertIntoTable(fileEntity);
            fileDto.setId(fileId);
            fileDto.setName(uploadDto.getName());
            fileDto.setMemeType(uploadDto.getMemeType());
            response.setData(fileDto);
            response.successful();
        } catch (SQLException e){
            response.setError(Error.databaseError);
        }
        return response;
    }

    public Response getFileInfo(DownloadDto downloadDto) {
        Response response = new Response();
        response.setTitle(Title.getFileInfo);
        FileEntity fileEntity = this.fileRepository.getFileInfo(downloadDto.getFileId());
        if (fileEntity.getId() == 0) {
            response.setError(Error.notFound);
            return response;
        }
        FileDto fileDto = new FileDto();
        fileDto.setId(fileEntity.getId());
        fileDto.setName(fileEntity.getName());
        fileDto.setMemeType(fileEntity.getMemeType());
        response.successful();
        response.setData(fileDto);
        return response;
    }

    public void download(DownloadDto downloadDto) {
        FileEntity fileEntity = this.fileRepository.getFileInfo(downloadDto.getFileId());
        this.sendFile(fileEntity.getPath());
    }

    private String receiveFile(UploadDto uploadDto) {
        int bytes = 0;
        String filePath = "";
        try {
            File file = File.createTempFile(
                    uploadDto.getUploadType().toString(),
                    "." + uploadDto.getMemeType(),
                    new File(this.resourcesDirectory)
            );
            filePath = file.getAbsolutePath();
            FileOutputStream fileOutputStream = new FileOutputStream(file);

            long size = this.dataInputStream.readLong();
            byte[] buffer = new byte[4 * 1024];
            while (size > 0 && (bytes = this.dataInputStream.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
                fileOutputStream.write(buffer, 0, bytes);
                size -= bytes;
            }
            System.out.println(String.format("%s file uploaded.", uploadDto.getUploadType()));
            fileOutputStream.close();
        } catch (IOException e){
            e.printStackTrace();
        }
        return filePath;
    }

    private void sendFile(String path){
        int bytes = 0;
        try {
            File file = new File(path);
            FileInputStream fileInputStream = new FileInputStream(file);

            this.dataOutputStream.writeLong(file.length());
            byte[] buffer = new byte[4 * 1024];
            while ((bytes = fileInputStream.read(buffer)) != -1) {
                this.dataOutputStream.write(buffer, 0, bytes);
                this.dataOutputStream.flush();
            }
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void lightClose() {
        this.fileRepository.closeConnection();
    }

    public void hardClose() {
        try {
            this.dataInputStream.close();
            this.dataOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.fileRepository.closeConnection();
    }
}

