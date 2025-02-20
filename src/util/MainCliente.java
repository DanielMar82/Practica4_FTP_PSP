package util;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;
import java.util.Scanner;

public class MainCliente {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        FTPClient cliente = new FTPClient();

        System.out.println("--- Ingresa la ip a la que te quieres conectar: ---");
        String serverFTP = sc.next();
        System.out.println("--- Ingresa el usuario: ---");
        String usuario = sc.next();
        System.out.println("--- Ingresa la contraseña: ---");
        String clave = sc.next();


        System.out.println("Nos vamos a conectar a "+ serverFTP);

        try {
            cliente.connect(serverFTP);
            System.out.println(cliente.getReplyString());
            int codigo = cliente.getReplyCode();

            if(!FTPReply.isPositiveCompletion(codigo)) {
                cliente.disconnect();
                System.out.println("Conexión rechazada");
                System.exit(0);
            }
            if(!cliente.login(usuario, clave)) {
                cliente.disconnect();
                System.out.println("Conexión rechazada");
                System.exit(0);
            }

            cliente.enterLocalPassiveMode();
//            System.out.println("Directorio actual:"+ cliente.printWorkingDirectory());

            int accion = 0;

            while(accion != 4) {
                System.out.println("\n--- Selecciona el número de la opción que desees realizar ---");
                System.out.println("1. Subir archivo");
                System.out.println("2. Bajar archivo");
                System.out.println("3. Mostrar archivos");
                System.out.println("4. Salir");
                accion = sc.nextInt();
                sc.nextLine();

                switch (accion) {
                    case 1:
                        if(!usuario.equalsIgnoreCase("anonymous")) {

                            //SUBIR ARCHIVOS
                            System.out.println("\n--- ¿Qué archivo deseas subir? ---");
                            String archivoSubir = sc.nextLine();
                            String ruta = "datos/"+archivoSubir;

                            try {
                                File archivo = new File(ruta);
//                                System.out.println("RUTA "+ archivo.getAbsolutePath());

                                if(!archivo.exists()) {
                                    System.out.println("\n--- ERROR: El archivo "+ archivoSubir +" no existe o no está en la carpeta 'datos/' ---");
                                    break;
                                }

                                cliente.setFileType(FTPClient.BINARY_FILE_TYPE);

                                BufferedInputStream in=new BufferedInputStream(new FileInputStream(archivo));
                                boolean subido = cliente.storeFile(archivoSubir,in);

                                if(subido) {
                                    System.out.println("\n--- El archivo se ha subido correctamente ---");
                                } else {
                                    System.out.println("\n--- ERROR: El archivo no se ha podido subir ---");
                                }

                            } catch (IOException e) {
                                System.out.println("Error al leer el archivo "+ e.getMessage());
                            }
                        } else {
                            System.out.println("\n--- ERROR: El usuario anonimo no tiene permiso para subir datos ---");
                        }
                        break;
                    case 2:

                        //BAJAR ARCHIVOS
                        cliente.setFileType(FTPClient.BINARY_FILE_TYPE);

                        System.out.println("\n--- ¿Qué archivo deseas bajar? ---");
                        String archivoBajar = sc.nextLine();
                        String rutaDestino = "descargas/"+ archivoBajar;

                        try {

                            FTPFile[] archivos = cliente.listFiles();
                            boolean existe = false;
                            for(FTPFile archivo : archivos) {
                                if(archivo.getName().equalsIgnoreCase(archivoBajar)) {
                                    existe = true;
                                }
                            }

                            if(!existe) {
                                System.out.println("\n--- ERROR: El archivo '"+ archivoBajar +"' no existe ---");
                                break;
                            }

                            File carpetaDescargas = new File("descargas");
                            if(!carpetaDescargas.exists()) {
                                carpetaDescargas.mkdir();
                            }

                            BufferedOutputStream out= new BufferedOutputStream(new FileOutputStream(rutaDestino));

                            boolean descargado = cliente.retrieveFile(archivoBajar, out);
                            out.close();

                            if(descargado) {
                                System.out.println("\n--- El archivo se ha descargado correctamente ---");
                            } else {
                                System.out.println("\n--- ERROR: El archivo no se ha podido descargar ---");
                            }
                        } catch (IOException e) {
                            System.out.println("Error al descargar el archivo "+ e.getMessage());
                        }
                        break;
                    case 3:
                        FTPFile[] archivosVer = cliente.listFiles();
                        System.out.println("\n--- Estos son los archivos que hay ---");
                        for(FTPFile archivo : archivosVer) {
                            if(archivo.getType() == 0) {
                                System.out.println("\t- "+ archivo.getName());
                            }
                        }
                        break;
                    case 4:
                        break;
                    default:
                        System.out.println("\n--- ERROR: La acción introducida es erronea ---");
                }
            }

            if(cliente.logout()) {
                System.out.println("\n--- Saliendo del servidor ---");
            } else {
                System.out.println("\nerror al salir del servidor");
            }

            cliente.disconnect();
            System.out.println("\nFin de la conexión");


        } catch (IOException e) {
            System.out.println("\n--- ERROR: No se ha podido conectar con el servidor: "+ e +" ---");
        }
    }
}