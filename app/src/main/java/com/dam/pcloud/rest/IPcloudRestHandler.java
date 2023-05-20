package com.dam.pcloud.rest;

import com.dam.pcloud.rest.HandlerCallBack;

public interface IPcloudRestHandler {

    /** Registra un usuario
     *
     * Al llamar a HandlerCallBack.onSuccess() se le pasará un Integer con el código de estado.
     *
     @param mail correo electronico del usuario
     @param password contraseña del usuario
     @param callback objeto de callback que gestionará el resultado
    */
    public void register(String mail, String password, HandlerCallBack callback);


    /** Inicia sesión usando el flujo de password digest
     *
     *  Al llamar a HandlerCallBack.onSuccess() se le pasará un Integer con el código de estado.
     *
     @param username nombre de usuario
     @param password contraseña del usuario
     @param callback objeto de callback que gestionará el resultado
    */
    public void login(String username, String password, HandlerCallBack callback);


    /** Manda las instrucciones para restablecer la contraseña al correo.
     *
     *  Al llamar a HandlerCallBack.onSuccess() se le pasará un Integer con el código de estado.
     *
     @param mail correo al que se mandará la información
     @param callback objeto de callback que gestionará el resultado
    */
    public void recover_password(String mail, HandlerCallBack callback);


    /** Crea un nuevo directorio.
     *
     * Requiere auth.
     *
     *  Al llamar a HandlerCallBack.onSuccess() se le pasará un PCloudFolder con la información del
     *  directorio creado.
     @param folder_id id del directorio padre
     @param name nombre del directorio a crear
     @param callback objeto de callback que gestionará el resultado
    */
    public void folder_create(String folder_id, String name, HandlerCallBack callback);


    /** Renombra un directorio.
     *
     * Requiere auth.
     *
     * Al llamar a HandlerCallBack.onSuccess() se le pasará un PCloudFolder con la información del
     * directorio renombrado.
     *
     @param folder_id id del directorio
     @param new_name nuevo nombre del directorio
     @param callback objeto de callback que gestionará el resultado
    */
    public void folder_rename(String folder_id, String new_name, HandlerCallBack callback);


    /** Mueve un directorio a otro directorio.
     *
     * Requiere auth.
     *
     * Al llamar a HandlerCallBack.onSuccess() se le pasará un PCloudFolder con la información del
     * directorio ya movido.
     *
     @param folder_id id del directorio
     @param new_parent_id id del nuevo directorio padre
     @param callback objeto de callback que gestionará el resultado
    */
    public void folder_move(String folder_id, String new_parent_id, HandlerCallBack callback);


    /** Copia un directorio a otro.
     *
     * Requiere auth.
     *
     * Al llamar a HandlerCallBack.onSuccess() se le pasará un PCloudFolder con la información del
     * directorio ya copiado.
     *
     @param folder_id id del directorio
     @param new_parent_id id del directorio al que será copiado
     @param callback objeto de callback que gestionará el resultado
    */
    public void folder_copy(String folder_id, String new_parent_id, HandlerCallBack callback);


    /** Borra el directorio indicado.
     *
     * Requiere auth.
     *
     * Al llamar a HandlerCallBack.onSuccess() se le pasará un PCloudFolder con la información del
     * directorio borrado.
     *
     @param folder_id id del directorio
     @param callback objeto de callback que gestionará el resultado
    */
    public void folder_delete(String folder_id, HandlerCallBack callback);


    /** Lista un directorio y su contenido.
     *
     * Requiere auth.
     *
     * Al llamar a HandlerCallBack.onSuccess() se le pasará un PCloudFolder con la información del
     * directorio con todos sus hijos.
     *
     @param folder_id id del directorio
     @param callback objeto de callback que gestionará el resultado
    */
    public void folder_list(String folder_id, HandlerCallBack callback);

//    // TODO terminar una vez esté completo
//    public void file_download(String folder_id, String file_name, String local_file_path, HandlerCallBack callback);


    /** Sube un fichero.
     *
     * Requiere auth.
     *
     * Al llamar a HandlerCallBack.onSuccess() se le pasará un PCloudFile con la información del
     * fichero subido.
     *
     @param folder_id id del directorio en el que se creará el fichero
     @param file_name nombre del archivo a crear
     @param local_file_path path hacia el fichero
     @param callback objeto de callback que gestionará el resultado
     */
    public void file_upload(String folder_id, String file_name, String local_file_path, HandlerCallBack callback);


    /** Copia un fichero a un directorio especificado.
     *
     * Requiere auth.
     *
     * Al llamar a HandlerCallBack.onSuccess() se le pasará un PCloudFile con la información del
     * fichero copiado.
     *
     @param file_id id del fichero
     @param to_folder_id id del directorio donde será copiado
     @param name nombre del fichero copiado
     @param callback objeto de callback que gestionará el resultado
    */
    public void file_copy(String file_id, String to_folder_id, String name, HandlerCallBack callback);


    /** Borra un fichero.
     *
     * Requiere auth.
     *
     * Al llamar a HandlerCallBack.onSuccess() se le pasará un PCloudFile con la información del
     * fichero borrado.
     *
     @param file_id id del fichero
     @param callback objeto de callback que gestionará el resultado
    */
    public void file_delete(String file_id, HandlerCallBack callback);


    /** Renombra un fichero.
     *
     * Requiere auth.
     *
     * Al llamar a HandlerCallBack.onSuccess() se le pasará un PCloudFile con la información del
     * fichero renombrado.
     *
     @param file_id id del fichero
     @param new_name nuevo nombre del archivo copiado
     @param callback objeto de callback que gestionará el resultado
    */
    public void file_rename(String file_id, String new_name, HandlerCallBack callback);


    /** Mueve un fichero a otro directorio.
     *
     * Requiere auth.
     *
     * Al llamar a HandlerCallBack.onSuccess() se le pasará un PCloudFile con la información del
     * fichero movido.
     *
     @param file_id id del fichero
     @param to_folder_id id del directorio donde será copiado
     @param new_name nuevo nombre del archivo
     @param callback objeto de callback que gestionará el resultado
     */
    public void file_move(String file_id, String to_folder_id, String new_name, HandlerCallBack callback);


    /** Consulta la inforamción de un fichero.
     *
     * Requiere auth.
     *
     * Al llamar a HandlerCallBack.onSuccess() se le pasará un PCloudFile con la información del
     * fichero consultado.
     *
     @param file_id id del fichero
     @param callback objeto de callback que gestionará el resultado
    */
    public void file_stat(String file_id, HandlerCallBack callback);

    /** Comprueba si el usuario ya ha iniciado sesión.
     */
    public boolean alreadyLogged();

}