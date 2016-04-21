import java.util.ArrayList;
import java.util.Random;


public class User 
{
	String userName;
   	String name;
   	String email;
   	String password;
   	String chattag;
   	Boolean online;
   	int role;
   	
   	public User(String name, String email, String password, int role, ArrayList<User> users)
	{
		this.userName = genUserName(users);
		this.name = name;
		this.email = email;
		this.password = password;
		online = false;
		
		if(name.contains(" "))
			chattag = name.substring(0, name.indexOf(" "));
		else
			chattag = name;
		
		this.role = role;
	}
	
	public static boolean exists(int index, String check, ArrayList<User> users)
	{
		boolean contains = false;
		switch(index)
		{
		case 0:
			for (int i = 0; i < users.size(); i++) 
		        if (users.get(i).getUserName().equals(check))
		           contains = true;
			break;
			
		case 1:
			for (int i = 0; i < users.size(); i++) 
		        if (users.get(i).getEmail().equals(check))
		        	contains = true;
			break;
		}
		
		return contains;
	}
	
	public String genUserName(ArrayList<User> users)
	{
		Random ran = new Random();
		String nUsername = Integer.toString(100000 + ran.nextInt(899999));
		
		while(exists(0, nUsername, users))
			nUsername = Integer.toString(100000 + ran.nextInt(899999));
		
		userName = nUsername;
		
		return userName;
	}
	
	public static int findPos(int index, String check, ArrayList<User> users)
	{
		int pos = 0;
		
		switch(index)
		{
		case 0:
			for (int i = 0; i < users.size(); i++)
			if(users.get(i).getUserName().equals(check))
				pos = i;
			break;
		case 1:
			for (int i = 0; i < users.size(); i++)
				if(users.get(i).getEmail().equals(check))
					pos = i;
				break;
		case 2:
			for (int i = 0; i < users.size(); i++)
				if(users.get(i).getChattag().equals(check))
					pos = i;
				break;
		}
		
		
		return pos;
	}
	
	public void setOnline(Boolean b)
	{
		online = b;
	}
	
	public String getUserName()
	{
		return userName;
	}
	
	public String getName()
	{
		return name;
	}
	
	public String getEmail()
	{
		return email;
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public String getChattag()
	{
		return chattag;
	}
	
	public Boolean getOnline()
	{
		return online;
	}
	
	public int getRole()
	{
		return role;
	}
}
