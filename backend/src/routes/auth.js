import { Router } from "express";
import bcrypt from "bcryptjs";
import { supabase } from "../config/supabase.js";

export const authRouter = Router();

authRouter.post("/register", async (req, res, next) => {
  try {
    const { name, email, password } = req.body;

    if (!name || !email || !password) {
      return res.status(400).json({ success: false, message: "name, email and password are required" });
    }

    const { data: existing, error: existingError } = await supabase
      .from("users")
      .select("id")
      .eq("email", email)
      .maybeSingle();

    if (existingError) throw existingError;
    if (existing) {
      return res.status(409).json({ success: false, message: "User already exists" });
    }

    const hashed = await bcrypt.hash(password, 10);

    const { data, error } = await supabase
      .from("users")
      .insert({ name, email, password: hashed })
      .select("id, name, email")
      .single();

    if (error) throw error;

    return res.status(201).json({ success: true, user: data });
  } catch (err) {
    return next(err);
  }
});

authRouter.post("/login", async (req, res, next) => {
  try {
    const { email, password } = req.body;

    if (!email || !password) {
      return res.status(400).json({ success: false, message: "email and password are required" });
    }

    const { data: user, error } = await supabase
      .from("users")
      .select("id, name, email, password")
      .eq("email", email)
      .maybeSingle();

    if (error) throw error;
    if (!user) {
      return res.status(401).json({ success: false, message: "Invalid credentials" });
    }

    const valid = await bcrypt.compare(password, user.password);
    if (!valid) {
      return res.status(401).json({ success: false, message: "Invalid credentials" });
    }

    return res.json({
      success: true,
      user: {
        id: user.id,
        name: user.name,
        email: user.email
      }
    });
  } catch (err) {
    return next(err);
  }
});

authRouter.post("/forgot-password", async (req, res, next) => {
  try {
    const { email, newPassword } = req.body;

    if (!email || !newPassword) {
      return res.status(400).json({ success: false, message: "email and newPassword are required" });
    }

    const hashed = await bcrypt.hash(newPassword, 10);

    const { data, error } = await supabase
      .from("users")
      .update({ password: hashed })
      .eq("email", email)
      .select("id")
      .maybeSingle();

    if (error) throw error;
    if (!data) {
      return res.status(404).json({ success: false, message: "Email not found" });
    }

    return res.json({ success: true, message: "Password updated" });
  } catch (err) {
    return next(err);
  }
});
