package jpa1;


import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MyApp {
    static EntityManagerFactory emf;
    static EntityManager em;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            // create connection
            emf = Persistence.createEntityManagerFactory("JPATest");
            em = emf.createEntityManager();
            try {
                while (true) {
                    System.out.println("1: add Apartment");
                    System.out.println("2: delete Apartment");
                    System.out.println("3: change Apartment");
                    System.out.println("4: view Apartment");
                    System.out.print("-> ");

                    String s = sc.nextLine();
                    switch (s) {
                        case "1":
                            addApartment(sc);
                            break;
                        case "2":
                            deleteApartment(sc);
                            break;
                        case "3":
                            changeApartment(sc);
                            break;
                        case "4":
                            viewApartment(sc);
                            break;
                        default:
                            return;
                    }
                }
            } finally {
                sc.close();
                em.close();
                emf.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
    }

    private static void addApartment(Scanner sc) {
        System.out.print("Enter district: ");
        String district = sc.nextLine();
        System.out.print("Enter address: ");
        String address = sc.nextLine();
        System.out.print("Enter apartment area: ");
        String areaS = sc.nextLine();
        Double area = Double.parseDouble(areaS);
        System.out.print("Enter number of rooms: ");
        String numberRoomsS = sc.nextLine();
        Integer numberRooms = Integer.parseInt(numberRoomsS);
        System.out.print("Enter price: ");
        String priceS = sc.nextLine();
        Integer price = Integer.parseInt(priceS);

        em.getTransaction().begin();
        try {
            Apartment apartment = new Apartment(district, address, area, numberRooms, price);
            em.persist(apartment);
            em.getTransaction().commit();

            System.out.println(apartment.getId());
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }
    }

    private static void deleteApartment(Scanner sc) {
        System.out.print("Enter apartment id: ");
        String idS = sc.nextLine();
        long id = Long.parseLong(idS);

        Apartment apartment = em.getReference(Apartment.class, id);

        if (apartment == null) {
            System.out.println("Apartment not found!");
            return;
        }

        em.getTransaction().begin();
        try {
            em.remove(apartment);
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }
    }

    private static void changeApartment(Scanner sc) {
        System.out.print("Enter apartment id: ");
        String idS = sc.nextLine();
        long id = Long.parseLong(idS);

        Apartment apartment = em.getReference(Apartment.class, id);
        if (apartment == null) {
            System.out.println("Apartment not found!");
            return;
        }

        System.out.print("Select field to edit: \n 1:district \n 2:address \n" +
                " 3:area \n 4:numberRooms \n 5:price \n 6:cancel \n");

        String resStr = sc.nextLine();
        int res = Integer.parseInt(resStr);
        while (res < 1 || res > 6) {
            System.out.println("selected = " + res + ", make choice 1...6");
            resStr = sc.nextLine();
            res = Integer.parseInt(resStr);
        }

        System.out.println("enter new information : ");
        em.getTransaction().begin();

        try {
            switch (res) {
                case 1:
                    String district = sc.nextLine();
                    apartment.setDistrict(district);
                    break;
                case 2:
                    String address = sc.nextLine();
                    apartment.setAddress(address);
                    break;
                case 3:
                    String area = sc.nextLine();
                    apartment.setArea(Double.parseDouble(area));
                    break;
                case 4:
                    String numberRooms = sc.nextLine();
                    apartment.setNumberRooms(Integer.parseInt(numberRooms));
                    break;
                case 5:
                    String price = sc.nextLine();
                    apartment.setPrice(Integer.parseInt(price));
                    break;
                case 6:
                    return;
            }

            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }
    }

    private static void viewApartment(Scanner sc) {
        Map<String, String> map = generateRequest(sc);
        StringBuilder sb = new StringBuilder();

        int n = 1;
        if (map.size() != 0) {
            for (String str : map.keySet()) {
                sb.append("a." + str).append("=" + map.get(str));
                if (map.size() > n) {
                    sb.append(" OR ");
                    n++;
                }
            }
        } else {
            System.out.println("Cancel");
            return;
        }
        try {
            Query query = em.createQuery("SELECT a FROM Apartment a WHERE " + sb, Apartment.class);

            List<Apartment> list = (List<Apartment>) query.getResultList();
            if (list.size() != 0) {
                for (Apartment a : list)
                    System.out.println(a);
            } else
                System.out.println("Search : No results were found for your search.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private static Map generateRequest(Scanner sc) {
        Map<String, String> map = new HashMap<>();

        System.out.print("Search: \n 1-district \n 2-address" +
                " \n 3-area \n 4-numberRooms \n 5-price \n 6-search\\cancel \n");

        String resStr;
        int res;
        try {
            do {
                System.out.print("make a selection, press 6 - to search or cancel :");
                resStr = sc.nextLine();
                res = Integer.parseInt(resStr);
                switch (res) {
                    case 1:
                        System.out.print("Enter district: ");
                        String district = sc.nextLine();
                        map.put("district", "'" + district + "'");
                        break;
                    case 2:
                        System.out.print("Enter address: ");
                        String address = sc.nextLine();
                        map.put("address", "'" + address + "'");
                        break;
                    case 3:
                        System.out.print("Enter apartment area: ");
                        String area = sc.nextLine();
                        map.put("area", area);
                        break;
                    case 4:
                        System.out.print("Enter number of rooms: ");
                        String numberRooms = sc.nextLine();
                        map.put("numberRooms", numberRooms);
                        break;
                    case 5:
                        System.out.print("Enter price: ");
                        String price = sc.nextLine();
                        map.put("price", price);
                        break;
                    case 6:
                        break;
                    default:
                        System.out.println("Enter from 1..6 or enter '6' to search: ");
                        continue;
                }

            } while (res != 6);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }
}