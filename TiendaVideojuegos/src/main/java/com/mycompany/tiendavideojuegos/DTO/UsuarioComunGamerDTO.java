    package com.mycompany.tiendavideojuegos.DTO;

    import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

    public class UsuarioComunGamerDTO extends UsuarioDTO
    {
        private String nickname;
        private Date fechaNacimiento;
        private String telefono;
        private String pais;
        private float saldoCartera;
        private String avatar;
        private boolean perfilPublico;
        private List<BibliotecaDTO> biblioteca = new ArrayList<>();
        
        public String getNickname() 
        {
            return nickname; 
        }

        public void setNickname(String nickname) 
        {
            this.nickname = nickname; 
        }

        public float getSaldoCartera() 
        {
            return saldoCartera; 
        }

        public void setSaldoCartera(float saldoCartera) 
        {
            this.saldoCartera = saldoCartera; 
        }

        public Date getFechaNacimiento() 
        {
            return fechaNacimiento; 
        }

        public void setFechaNacimiento(Date fechaNacimiento) 
        {
            this.fechaNacimiento = fechaNacimiento; 
        }

        public String getTelefono() 
        {
            return telefono; 
        }

        public void setTelefono(String telefono) 
        {
            this.telefono = telefono; 
        }

        public String getPais() 
        {
            return pais; 
        }

        public void setPais(String pais) 
        {
            this.pais = pais;
        }

        public String getAvatar() 
        {
            return avatar;
        }

        public void setAvatar(String avatar) 
        {
            this.avatar = avatar;
        }

        public boolean isPerfilPublico()
        {
            return perfilPublico;
        }

        public void setPerfilPublico(boolean perfilPublico)
        {
            this.perfilPublico = perfilPublico;
        }
        
        public List<BibliotecaDTO> getBiblioteca()
        {
            return biblioteca;
        }
        
        public void setBiblioteca(List<BibliotecaDTO> biblioteca)
        {
            this.biblioteca = biblioteca;
        }
    }
