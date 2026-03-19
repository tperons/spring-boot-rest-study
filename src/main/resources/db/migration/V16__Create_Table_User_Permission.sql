CREATE TABLE public.tb_user_permission (
    user_id bigint NOT NULL,
    permission_id bigint NOT NULL,
    PRIMARY KEY (user_id, permission_id),
    CONSTRAINT fk_user_permission_user FOREIGN KEY (user_id) REFERENCES public.tb_user(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_permission_permission FOREIGN KEY (permission_id) REFERENCES public.tb_permission(id) ON DELETE CASCADE
);
