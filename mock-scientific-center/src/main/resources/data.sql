INSERT INTO public.scientific_area(id, name) VALUES (1, 'area1');
INSERT INTO public.scientific_area(id, name) VALUES (2, 'area2');

INSERT INTO public.magazine(id, isbn, name, price, who_pays, email) VALUES (1, '111-222', 'magazine1', 121, 'sellers', 'test@gmail.com');
INSERT INTO public.magazine(id, isbn, name, price, who_pays, email) VALUES (2, '222-333', 'magazine2', 321, 'buyers' , 'mail@gmail.com');
INSERT INTO public.magazine(id, isbn, name, price, who_pays, email) VALUES (3, '444-333', 'magazine2', 321, 'buyers' , 'maja@gmail.com');

INSERT INTO public.magazine_scientific_area(magazine_id, scientific_area_id) VALUES (1, 1);
INSERT INTO public.magazine_scientific_area(magazine_id, scientific_area_id) VALUES (2, 2);
INSERT INTO public.magazine_scientific_area(magazine_id, scientific_area_id) VALUES (3, 2);